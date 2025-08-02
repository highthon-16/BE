package com.ittae.be.controller

import com.ittae.be.dto.SetAiStyleRequest
import com.ittae.be.dto.SetGoalRequest
import com.ittae.be.dto.UserResponse
import com.ittae.be.service.UserDetailsImpl
import com.ittae.be.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @PostMapping("/goal")
    fun setGoal(
        @Valid @RequestBody setGoalRequest: SetGoalRequest,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): UserResponse {
        return userService.setGoal(setGoalRequest, userDetails.getUserId()!!)
    }

    @PostMapping("/ai-style")
    fun setAiStyle(
        @Valid @RequestBody setAiStyleRequest: SetAiStyleRequest,
        @AuthenticationPrincipal userDetails: UserDetailsImpl
    ): UserResponse {
        return userService.setAiStyle(setAiStyleRequest, userDetails.getUserId()!!)
    }
}