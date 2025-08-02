package com.ittae.be.service

import com.ittae.be.client.McpCalendarClient
import com.ittae.be.model.CalendarEvent
import com.ittae.be.repository.CalendarEventRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CalendarService(
    private val mcpCalendarClient: McpCalendarClient,
    private val calendarEventRepository: CalendarEventRepository
) {

    fun getEvents(userId: Long): Flux<CalendarEvent> {
        return mcpCalendarClient.getEvents(userId)
    }

    fun addEvent(event: CalendarEvent): Mono<CalendarEvent> {
        return mcpCalendarClient.addEvent(event)
            .doOnSuccess { calendarEventRepository.save(it) }
    }

    fun updateEvent(eventId: Long, event: CalendarEvent): Mono<CalendarEvent> {
        return mcpCalendarClient.updateEvent(eventId, event)
            .doOnSuccess { calendarEventRepository.save(it) }
    }

    fun deleteEvent(eventId: Long): Mono<Void> {
        return mcpCalendarClient.deleteEvent(eventId)
            .doOnSuccess { calendarEventRepository.deleteById(eventId) }
    }
}
