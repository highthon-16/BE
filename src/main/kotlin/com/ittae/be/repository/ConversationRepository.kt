package com.ittae.be.repository

import com.ittae.be.model.Conversation
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationRepository : JpaRepository<Conversation, Long>
