package com.ittae.be.dto

import com.ittae.be.model.AiStyle
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class SetAiStyleRequest(
    @field:NotNull(message = "AI 스타일은 필수입니다")
    @field:Schema(
        description = "AI 스타일 설정", 
        example = "ADULT_WOMAN",
        allowableValues = ["TEENAGE_GIRL", "TEENAGE_BOY", "ADULT_WOMAN", "ADULT_MAN"]
    )
    val aiStyle: AiStyle
)