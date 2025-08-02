package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    val token: String,

    val expiresAt: LocalDateTime,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var revoked: Boolean = false
)
