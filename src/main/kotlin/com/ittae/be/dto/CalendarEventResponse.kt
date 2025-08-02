package com.ittae.be.dto



data class CalendarEventResponse(
    val id: Long?,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: String,
    val duration: Int,
    val category: String,
    val staminaCost: Int?,
    val status: String,
    val staminaAfterCompletion: Int?,
    val createdAt: String
)
