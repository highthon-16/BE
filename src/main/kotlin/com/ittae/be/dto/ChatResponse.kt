package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ChatResponse(
    @field:Schema(description = "AI 응답 메시지")
    val message: String,
    
    @field:Schema(description = "채팅 세션 ID")
    val sessionId: String,
    
    @field:Schema(description = "실행된 액션 정보 (선택사항)")
    val actionPerformed: ActionInfo? = null,
    
    @field:Schema(description = "응답 타임스탬프")
    val timestamp: String
)

data class ActionInfo(
    @field:Schema(description = "실행된 액션 타입", example = "CREATE_EVENT")
    val actionType: String,
    
    @field:Schema(description = "액션 결과")
    val result: Any? = null,
    
    @field:Schema(description = "액션 성공 여부")
    val success: Boolean
)
