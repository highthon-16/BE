package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SetGoalRequest(
    @field:NotBlank(message = "목표는 비어있을 수 없습니다")
    @field:Size(max = 500, message = "목표는 500자를 초과할 수 없습니다")
    @field:Schema(description = "사용자 목표", example = "건강한 생활 습관 만들기")
    val goals: String
)