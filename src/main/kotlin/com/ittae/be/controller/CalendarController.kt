package com.ittae.be.controller

import com.ittae.be.model.CalendarEvent
import com.ittae.be.service.CalendarService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping("/{userId}/events")
    fun getEvents(@PathVariable userId: Long): Flux<CalendarEvent> {
        return calendarService.getEvents(userId)
    }

    @PostMapping("/events")
    fun addEvent(@RequestBody event: CalendarEvent): Mono<CalendarEvent> {
        return calendarService.addEvent(event)
    }

    @PutMapping("/events/{eventId}")
    fun updateEvent(@PathVariable eventId: Long, @RequestBody event: CalendarEvent): Mono<CalendarEvent> {
        return calendarService.updateEvent(eventId, event)
    }

    @DeleteMapping("/events/{eventId}")
    fun deleteEvent(@PathVariable eventId: Long): Mono<Void> {
        return calendarService.deleteEvent(eventId)
    }
}
