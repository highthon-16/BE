package com.ittae.be.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이벤트 카테고리", example = "WORK")
enum class EventCategory(private val value: String) {
    @Schema(description = "학습") STUDY("STUDY"),
    @Schema(description = "업무") WORK("WORK"),
    @Schema(description = "휴식") REST("REST"),
    @Schema(description = "활동") ACTIVITY("ACTIVITY");

    @JsonValue
    fun getValue(): String = value

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): EventCategory {
            return values().find { 
                it.value.equals(value, ignoreCase = true) || 
                it.name.equals(value, ignoreCase = true) 
            } ?: throw IllegalArgumentException("Invalid EventCategory: $value. Valid values are: ${values().joinToString { it.value }}")
        }
    }
}
