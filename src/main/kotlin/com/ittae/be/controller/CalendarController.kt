package com.ittae.be.controller

import com.ittae.be.dto.CalendarEventRequest
import com.ittae.be.dto.CalendarEventResponse
import com.ittae.be.service.CalendarService
import com.ittae.be.service.UserDetailsImpl
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
@RestController
@RequestMapping("/api/calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping("/events")
    fun getEvents(@AuthenticationPrincipal userDetails: UserDetailsImpl): Flux<CalendarEventResponse> {
        return calendarService.getEventsByUserId(userDetails.getUserId()!!)
    }

    @PostMapping("/events")
    fun addEvent(
        @Valid @RequestBody request: CalendarEventRequest,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): Mono<CalendarEventResponse> {
        return calendarService.addEvent(request, userDetails.getUserId()!!)
    }

    @PutMapping("/events/{eventId}")
    fun updateEvent(
        @PathVariable eventId: Long,
        @Valid @RequestBody request: CalendarEventRequest,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): Mono<CalendarEventResponse> {
        return calendarService.updateEvent(eventId, request, userDetails.getUserId()!!)
    }
    @DeleteMapping("/events/{eventId}")
    fun deleteEvent(
        @PathVariable eventId: Long,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): Mono<Void> {
        return calendarService.deleteEvent(eventId, userDetails.getUserId()!!)
    }
}