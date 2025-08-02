package com.ittae.be.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "conversations")
class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    val message: String,

    val sender: String,

    val aiStyle: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
