package com.ittae.be.client

import com.ittae.be.model.CalendarEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class McpCalendarClient(private val webClient: WebClient) {

    fun getEvents(userId: Long): Flux<CalendarEvent> {
        return webClient.get()
            .uri("/users/{userId}/events", userId)
            .retrieve()
            .bodyToFlux(CalendarEvent::class.java)
    }

    fun addEvent(event: CalendarEvent): Mono<CalendarEvent> {
        return webClient.post()
            .uri("/events")
            .bodyValue(event)
            .retrieve()
            .bodyToMono(CalendarEvent::class.java)
    }

    fun updateEvent(eventId: Long, event: CalendarEvent): Mono<CalendarEvent> {
        return webClient.put()
            .uri("/events/{eventId}", eventId)
            .bodyValue(event)
            .retrieve()
            .bodyToMono(CalendarEvent::class.java)
    }

    fun deleteEvent(eventId: Long): Mono<Void> {
        return webClient.delete()
            .uri("/events/{eventId}", eventId)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
}
