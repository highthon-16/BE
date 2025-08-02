package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class ChatRequest(
    @field:Schema(description = "사용자 메시지", example = "내일 오전 10시에 팀 미팅 일정을 만들어줘")
    @field:NotBlank(message = "메시지는 필수입니다")
    val message: String,
    
    @field:Schema(description = "채팅 세션 ID (선택사항)", example = "chat-session-123")
    val sessionId: String? = null
)
