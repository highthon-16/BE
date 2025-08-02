package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var username: String,

    var passwordHash: String,

    var email: String,

    var goals: String? = null,

    var currentStamina: Int = 100,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)
