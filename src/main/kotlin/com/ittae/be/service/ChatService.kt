package com.ittae.be.service

import com.ittae.be.dto.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ChatService {

    private val logger = LoggerFactory.getLogger(ChatService::class.java)

    fun processChat(request: ChatRequest, userId: Long): Mono<ChatResponse> {
        val sessionId = request.sessionId ?: generateSessionId()
        
        logger.info("Processing chat request for user: $userId, session: $sessionId")
        
        // 임시로 간단한 응답만 반환
        val response = ChatResponse(
            message = "채팅 기능이 구현 중입니다. 메시지: ${request.message}",
            sessionId = sessionId,
            actionPerformed = null,
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        
        return Mono.just(response)
    }

    private fun generateSessionId(): String {
        return "chat-${UUID.randomUUID().toString().substring(0, 8)}"
    }
}
