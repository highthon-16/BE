package com.ittae.be.dto

import com.ittae.be.model.EventCategory
import com.ittae.be.model.EventStatus
import java.time.LocalDateTime

data class CalendarEventResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: LocalDateTime,
    val duration: Int,
    val category: EventCategory,
    val staminaCost: Int,
    val status: EventStatus,
    val staminaAfterCompletion: Int?,
    val createdAt: LocalDateTime
)
