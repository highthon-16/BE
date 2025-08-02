package com.ittae.be.service

import com.ittae.be.client.McpServerClient
import com.ittae.be.dto.CalendarEventRequest
import com.ittae.be.dto.CalendarEventResponse
import com.ittae.be.model.CalendarEvent
import com.ittae.be.model.EventStatus
import com.ittae.be.model.EventCategory
import com.ittae.be.repository.CalendarEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class CalendarService(
    private val mcpServerClient: McpServerClient,
    private val calendarEventRepository: CalendarEventRepository
) {

    private val logger = LoggerFactory.getLogger(CalendarService::class.java)

    fun getAllEvents(userId: Long): Mono<List<CalendarEventResponse>> {
        logger.info("Getting all events for user: {}", userId)
        
        return Mono.fromCallable {
            calendarEventRepository.findByUserId(userId)
                .map { toResponse(it) }
        }
        .doOnSuccess { events ->
            logger.info("Successfully retrieved {} events for user: {}", events.size, userId)
        }
        .doOnError { error ->
            logger.error("Failed to get events for user: {}", userId, error)
        }
    }

    fun getEventsByDate(date: String, userId: Long): Mono<List<CalendarEventResponse>> {
        logger.info("Getting events for user: {} on date: {}", userId, date)
        
        return getAllEvents(userId)
            .map { events ->
                events.filter { event ->
                    event.startTime.startsWith(date)
                }
            }
    }

    fun createEvent(request: CalendarEventRequest, userId: Long): Mono<CalendarEventResponse> {
        logger.info("Creating event for user: {}, title: {}", userId, request.title)
        
        return try {
            val event = CalendarEvent(
                userId = userId,
                title = request.title,
                description = request.description,
                location = request.location,
                startTime = request.startTime,
                duration = request.duration,
                category = EventCategory.fromString(request.category),
                staminaCost = request.staminaCost,
                status = request.status ?: EventStatus.PLANNED,
                createdAt = request.createdAt ?: LocalDateTime.now()
            )
            
            val savedEvent = calendarEventRepository.save(event)
            logger.info("Successfully created event: {} for user: {}", savedEvent.id, userId)
            
            // MCP 서버에도 알림
            val mcpArgs = mapOf(
                "user_id" to userId,
                "event_id" to savedEvent.id,
                "title" to savedEvent.title,
                "start_time" to savedEvent.startTime,
                "duration" to savedEvent.duration,
                "category" to savedEvent.category.getValue()
            )
            
            mcpServerClient.callMcpFunction("create_calendar_event", mcpArgs)
                .doOnSuccess { 
                    logger.info("MCP server notified of event creation: {}", savedEvent.id)
                }
                .doOnError { error ->
                    logger.warn("Failed to notify MCP server of event creation: {}", error.message)
                }
                .subscribe { }
            
            Mono.just(toResponse(savedEvent))
            
        } catch (e: Exception) {
            logger.error("Failed to create event for user: {}", userId, e)
            Mono.error(e)
        }
    }

    fun updateEvent(eventId: Long, request: CalendarEventRequest, userId: Long): Mono<CalendarEventResponse> {
        logger.info("Updating event: {} for user: {}", eventId, userId)
        
        return Mono.fromCallable {
            val existingEvent = calendarEventRepository.findByIdAndUserId(eventId, userId)
                ?: throw IllegalArgumentException("Event not found or access denied")
            
            val updatedEvent = existingEvent.copy(
                title = request.title,
                description = request.description,
                location = request.location,
                startTime = request.startTime,
                duration = request.duration,
                category = EventCategory.fromString(request.category),
                staminaCost = request.staminaCost,
                status = request.status ?: existingEvent.status
            )
            
            val savedEvent = calendarEventRepository.save(updatedEvent)
            logger.info("Successfully updated event: {} for user: {}", eventId, userId)
            
            // MCP 서버에도 알림
            val mcpArgs = mapOf(
                "user_id" to userId,
                "event_id" to eventId,
                "title" to savedEvent.title,
                "start_time" to savedEvent.startTime,
                "duration" to savedEvent.duration,
                "category" to savedEvent.category.getValue()
            )
            
            mcpServerClient.callMcpFunction("update_calendar_event", mcpArgs)
                .doOnSuccess { 
                    logger.info("MCP server notified of event update: {}", eventId)
                }
                .doOnError { error ->
                    logger.warn("Failed to notify MCP server of event update: {}", error.message)
                }
                .subscribe { }
            
            toResponse(savedEvent)
        }
        .doOnError { error ->
            logger.error("Failed to update event: {} for user: {}", eventId, userId, error)
        }
    }

    fun deleteEvent(eventId: Long, userId: Long): Mono<Void> {
        logger.info("Deleting event: {} for user: {}", eventId, userId)
        
        return Mono.fromRunnable<Void> {
            val existingEvent = calendarEventRepository.findByIdAndUserId(eventId, userId)
                ?: throw IllegalArgumentException("Event not found or access denied")
            
            calendarEventRepository.deleteById(eventId)
            logger.info("Successfully deleted event: {} for user: {}", eventId, userId)
            
            // MCP 서버에도 알림
            val mcpArgs = mapOf(
                "user_id" to userId,
                "event_id" to eventId
            )
            
            mcpServerClient.callMcpFunction("delete_calendar_event", mcpArgs)
                .doOnSuccess { 
                    logger.info("MCP server notified of event deletion: {}", eventId)
                }
                .doOnError { error ->
                    logger.warn("Failed to notify MCP server of event deletion: {}", error.message)
                }
                .subscribe { }
        }
        .doOnError { error ->
            logger.error("Failed to delete event: {} for user: {}", eventId, userId, error)
        }
    }

    private fun toResponse(event: CalendarEvent): CalendarEventResponse {
        return CalendarEventResponse(
            id = event.id,
            title = event.title,
            description = event.description,
            location = event.location,
            startTime = event.startTime,
            duration = event.duration,
            category = event.category.getValue(),
            staminaCost = event.staminaCost,
            status = event.status.getValue(),
            staminaAfterCompletion = event.staminaAfterCompletion,
            createdAt = event.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}
