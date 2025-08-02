package com.ittae.be.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpRequest(
    @field:NotBlank(message = "사용자명은 필수입니다")
    @field:Size(min = 2, max = 20, message = "사용자명은 2자 이상 20자 이하여야 합니다")
    @field:Schema(description = "사용자명", example = "강시우")
    val username: String,
    
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:Schema(description = "이메일", example = "user@example.com")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하여야 합니다")
    @field:Schema(description = "비밀번호", example = "password123")
    val password: String
)
