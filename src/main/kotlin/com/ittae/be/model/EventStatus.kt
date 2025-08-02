package com.ittae.be.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이벤트 상태", example = "PLANNED")
enum class EventStatus(private val value: String) {
    @Schema(description = "계획됨") PLANNED("PLANNED"),
    @Schema(description = "완료됨") COMPLETED("COMPLETED"),
    @Schema(description = "취소됨") CANCELED("CANCELED");

    @JsonValue
    fun getValue(): String = value

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): EventStatus {
            return values().find { 
                it.value.equals(value, ignoreCase = true) || 
                it.name.equals(value, ignoreCase = true) 
            } ?: throw IllegalArgumentException("Invalid EventStatus: $value. Valid values are: ${values().joinToString { it.value }}")
        }
    }
}
