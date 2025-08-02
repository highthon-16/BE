package com.ittae.be.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "AI 스타일", example = "ADULT_WOMAN")
enum class AiStyle(private val value: String) {
    @Schema(description = "10대 여성") TEENAGE_GIRL("TEENAGE_GIRL"),
    @Schema(description = "10대 남성") TEENAGE_BOY("TEENAGE_BOY"), 
    @Schema(description = "성인 여성") ADULT_WOMAN("ADULT_WOMAN"),
    @Schema(description = "성인 남성") ADULT_MAN("ADULT_MAN");

    @JsonValue
    fun getValue(): String = value

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromString(value: String): AiStyle {
            return values().find { 
                it.value.equals(value, ignoreCase = true) || 
                it.name.equals(value, ignoreCase = true) 
            } ?: throw IllegalArgumentException("Invalid AiStyle: $value. Valid values are: ${values().joinToString { it.value }}")
        }
    }
}
