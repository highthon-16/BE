package com.ittae.be.controller

import com.ittae.be.dto.LoginRequest
import com.ittae.be.dto.SignUpRequest
import com.ittae.be.dto.TokenResponse
import com.ittae.be.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        return try {
            val user = authService.signUp(signUpRequest)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        return try {
            val tokenResponse = authService.login(loginRequest)
            ResponseEntity.ok(tokenResponse)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
