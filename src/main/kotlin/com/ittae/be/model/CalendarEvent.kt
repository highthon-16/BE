package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_events")
class CalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id")
    var userId: Long = 0,

    var title: String,

    var description: String? = null,

    var location: String? = null,

    var startTime: LocalDateTime,

    var duration: Int,

    @Enumerated(EnumType.STRING)
    var category: EventCategory,

    var staminaCost: Int = 0,

    @Enumerated(EnumType.STRING)
    var status: EventStatus = EventStatus.PLANNED,

    var staminaAfterCompletion: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)