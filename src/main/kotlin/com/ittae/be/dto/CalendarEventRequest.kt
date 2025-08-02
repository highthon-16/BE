package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class CalendarEventRequest(
    @field:Schema(description = "이벤트 제목", example = "팀 미팅")
    @field:NotNull(message = "제목은 필수입니다")
    val title: String,
    
    @field:Schema(description = "이벤트 설명", example = "주간 팀 미팅")
    val description: String? = null,
    
    @field:Schema(description = "장소", example = "회의실 A")
    val location: String? = null,
    
    @field:Schema(description = "시작 시간", example = "2025-08-03T10:00:00")
    @field:NotNull(message = "시작 시간은 필수입니다")
    val startTime: String,
    
    @field:Schema(description = "지속 시간(분)", example = "60")
    val duration: Int,
    
    @field:Schema(
        description = "카테고리", 
        example = "WORK",
        allowableValues = ["STUDY", "WORK", "REST", "ACTIVITY"]
    )
    @field:NotNull(message = "카테고리는 필수입니다")
    val category: String,
    
    @field:Schema(description = "스태미나 소모량", example = "20")
    val staminaCost: Int? = null,
    
    @field:Schema(description = "생성 시간 (선택사항, 생략시 현재 시간)", example = "2025-08-03T09:00:00")
    val createdAt: java.time.LocalDateTime? = null,
    
    @field:Schema(
        description = "이벤트 상태 (선택사항, 생략시 PLANNED)", 
        example = "PLANNED",
        allowableValues = ["PLANNED", "COMPLETED", "CANCELED"]
    )
    val status: com.ittae.be.model.EventStatus? = null
)
