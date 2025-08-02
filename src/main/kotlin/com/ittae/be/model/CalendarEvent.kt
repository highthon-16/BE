package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_events")
data class CalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id")
    val userId: Long = 0,

    val title: String,

    val description: String? = null,

    val location: String? = null,

    val startTime: String,

    val duration: Int,

    @Enumerated(EnumType.STRING)
    val category: EventCategory,

    val staminaCost: Int? = null,

    @Enumerated(EnumType.STRING)
    val status: EventStatus = EventStatus.PLANNED,

    val staminaAfterCompletion: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
