package com.ittae.be.dto

import com.ittae.be.model.AiStyle

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val goals: String?,
    val aiStyle: AiStyle,
    val currentStamina: Int
)
