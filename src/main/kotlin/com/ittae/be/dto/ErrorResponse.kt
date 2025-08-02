package com.ittae.be.dto

data class ErrorResponse(
    val message: String,
    val code: String? = null
)
