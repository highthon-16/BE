package com.ittae.be.controller

import com.ittae.be.dto.ChatRequest
import com.ittae.be.dto.ChatResponse
import com.ittae.be.service.ChatService
import com.ittae.be.service.UserDetailsImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat API", description = "AI 챗봇과 캘린더 연동 API")
class ChatController(private val chatService: ChatService) {

    @PostMapping("/message")
    @Operation(summary = "AI 챗봇에게 메시지 전송")
    fun sendMessage(
        @Valid @RequestBody request: ChatRequest,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): Mono<ChatResponse> {
        return chatService.processChat(request, userDetails.getUserId()!!)
    }

    @GetMapping("/health")
    @Operation(summary = "채팅 서비스 상태 확인")
    fun healthCheck(): Mono<Map<String, String>> {
        return Mono.just(
            mapOf(
                "status" to "healthy",
                "service" to "chat-api",
                "timestamp" to java.time.LocalDateTime.now().toString()
            )
        )
    }
}
