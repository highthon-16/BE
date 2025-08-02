package com.ittae.be.controller

import com.ittae.be.dto.LoginRequest
import com.ittae.be.dto.SignUpRequest
import com.ittae.be.dto.TokenResponse
import com.ittae.be.dto.UserResponse
import com.ittae.be.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody signUpRequest: SignUpRequest): UserResponse {
        return authService.signUp(signUpRequest)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): TokenResponse {
        return authService.login(loginRequest)
    }
}
