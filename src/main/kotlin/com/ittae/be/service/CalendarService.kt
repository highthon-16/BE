package com.ittae.be.service

import com.ittae.be.client.McpCalendarClient
import com.ittae.be.dto.CalendarEventRequest
import com.ittae.be.dto.CalendarEventResponse
import com.ittae.be.exception.EventNotFoundException
import com.ittae.be.model.CalendarEvent
import com.ittae.be.model.EventStatus
import com.ittae.be.repository.CalendarEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class CalendarService(
    private val mcpCalendarClient: McpCalendarClient,
    private val calendarEventRepository: CalendarEventRepository
) {

    private val logger = LoggerFactory.getLogger(CalendarService::class.java)

    fun getEventsByUserId(userId: Long): Flux<CalendarEventResponse> {
        logger.info("Getting events for user: {}", userId)
        return mcpCalendarClient.getEvents(userId)
            .map { toResponse(it) }
            .doOnComplete { logger.info("Successfully retrieved events for user: {}", userId) }
            .doOnError { error -> logger.error("Failed to get events for user: {}", userId, error) }
    }

    fun addEvent(request: CalendarEventRequest, userId: Long): Mono<CalendarEventResponse> {
        logger.info("Adding event for user: {}, title: {}", userId, request.title)
        
        try {
            val event = CalendarEvent(
                userId = userId,
                title = request.title,
                description = request.description,
                location = request.location,
                startTime = request.startTime,
                duration = request.duration,
                category = request.category,
                staminaCost = request.staminaCost,
                status = request.status ?: EventStatus.PLANNED,
                createdAt = request.createdAt ?: LocalDateTime.now()
            )
            
            return mcpCalendarClient.addEvent(event)
                .doOnSuccess { 
                    logger.info("Successfully added event: {} for user: {}", it.id, userId)
                    calendarEventRepository.save(it) 
                }
                .doOnError { error -> 
                    logger.error("Failed to add event for user: {}, title: {}", userId, request.title, error)
                }
                .map { toResponse(it) }
        } catch (e: Exception) {
            logger.error("Failed to create event object for user: {}", userId, e)
            throw e
        }
    }

    fun updateEvent(eventId: Long, request: CalendarEventRequest, userId: Long): Mono<CalendarEventResponse> {
        logger.info("Updating event: {} for user: {}", eventId, userId)
        
        try {
            val event = CalendarEvent(
                id = eventId,
                userId = userId,
                title = request.title,
                description = request.description,
                location = request.location,
                startTime = request.startTime,
                duration = request.duration,
                category = request.category,
                staminaCost = request.staminaCost,
                status = request.status ?: EventStatus.PLANNED,
                createdAt = request.createdAt ?: LocalDateTime.now()
            )
            
            return mcpCalendarClient.updateEvent(eventId, event)
                .doOnSuccess { 
                    logger.info("Successfully updated event: {} for user: {}", eventId, userId)
                    calendarEventRepository.save(it) 
                }
                .doOnError { error -> 
                    logger.error("Failed to update event: {} for user: {}", eventId, userId, error)
                }
                .map { toResponse(it) }
        } catch (e: Exception) {
            logger.error("Failed to create event object for update, eventId: {}, userId: {}", eventId, userId, e)
            throw e
        }
    }

    fun deleteEvent(eventId: Long, userId: Long): Mono<Void> {
        logger.info("Deleting event: {} for user: {}", eventId, userId)
        
        return mcpCalendarClient.deleteEvent(eventId)
            .doOnSuccess { 
                logger.info("Successfully deleted event: {} for user: {}", eventId, userId)
                calendarEventRepository.deleteById(eventId) 
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
            category = event.category,
            staminaCost = event.staminaCost,
            status = event.status,
            staminaAfterCompletion = event.staminaAfterCompletion,
            createdAt = event.createdAt
        )
    }
}
