package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_events")
class CalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    var title: String,

    var description: String? = null,

    var location: String? = null,

    var startTime: LocalDateTime,

    var duration: Int,

    var category: String,

    var staminaCost: Int = 0,

    var status: String = "planned",

    var staminaAfterCompletion: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
