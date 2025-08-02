package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:Schema(description = "이메일", example = "user@example.com")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Schema(description = "비밀번호", example = "password123")
    val password: String
)
