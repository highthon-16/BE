package com.ittae.be.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class GeminiClient(
    @Qualifier("geminiWebClient") private val webClient: WebClient,
    @Value("\${gemini.api.key:}") private val apiKey: String
) {

    fun generateContent(prompt: String, tools: List<FunctionDeclaration>? = null): Mono<GeminiResponse> {
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            tools = tools?.let { listOf(Tool(functionDeclarations = it)) }
        )

        return webClient.post()
            .uri("/v1beta/models/gemini-2.5-flash:generateContent")
            .header("Content-Type", "application/json")
            .header("X-goog-api-key", apiKey)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GeminiResponse::class.java)
            .retryWhen(
                reactor.util.retry.Retry.backoff(3, java.time.Duration.ofSeconds(2))
                    .filter { it is org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests }
            )
            .onErrorReturn(
                GeminiResponse(
                    candidates = null,
                    error = ErrorInfo(429, "API 요청 한도가 초과되었습니다. 잠시 후 다시 시도해주세요.", "RESOURCE_EXHAUSTED")
                )
            )
    }

    fun generateContentWithCalendarTools(prompt: String): Mono<GeminiResponse> {
        val tools = listOf(
            FunctionDeclaration(
                name = "create_calendar_event",
                description = "새로운 캘린더 이벤트를 생성합니다",
                parameters = FunctionParameters(
                    type = "object",
                    properties = mapOf(
                        "title" to PropertySchema("string", "이벤트 제목"),
                        "start_time" to PropertySchema("string", "시작 시간 (ISO 형식: 2025-08-03T10:00:00)"),
                        "duration" to PropertySchema("integer", "지속 시간(분)"),
                        "category" to PropertySchema("string", "카테고리 (STUDY, WORK, REST, ACTIVITY)"),
                        "description" to PropertySchema("string", "이벤트 설명"),
                        "location" to PropertySchema("string", "장소"),
                        "stamina_cost" to PropertySchema("integer", "스태미나 소모량")
                    ),
                    required = listOf("title", "start_time", "duration", "category")
                )
            ),
            FunctionDeclaration(
                name = "get_all_events",
                description = "모든 캘린더 이벤트를 조회합니다",
                parameters = FunctionParameters(
                    type = "object",
                    properties = emptyMap(),
                    required = emptyList()
                )
            ),
            FunctionDeclaration(
                name = "update_calendar_event",
                description = "기존 캘린더 이벤트를 수정합니다",
                parameters = FunctionParameters(
                    type = "object",
                    properties = mapOf(
                        "event_id" to PropertySchema("integer", "수정할 이벤트 ID"),
                        "title" to PropertySchema("string", "이벤트 제목"),
                        "start_time" to PropertySchema("string", "시작 시간 (ISO 형식)"),
                        "duration" to PropertySchema("integer", "지속 시간(분)"),
                        "category" to PropertySchema("string", "카테고리"),
                        "description" to PropertySchema("string", "이벤트 설명"),
                        "location" to PropertySchema("string", "장소"),
                        "stamina_cost" to PropertySchema("integer", "스태미나 소모량"),
                        "status" to PropertySchema("string", "이벤트 상태 (PLANNED, COMPLETED, CANCELED)")
                    ),
                    required = listOf("event_id", "title", "start_time", "duration", "category")
                )
            ),
            FunctionDeclaration(
                name = "delete_calendar_event",
                description = "캘린더 이벤트를 삭제합니다",
                parameters = FunctionParameters(
                    type = "object",
                    properties = mapOf(
                        "event_id" to PropertySchema("integer", "삭제할 이벤트 ID")
                    ),
                    required = listOf("event_id")
                )
            ),
            FunctionDeclaration(
                name = "get_events_by_date",
                description = "특정 날짜의 이벤트를 조회합니다",
                parameters = FunctionParameters(
                    type = "object",
                    properties = mapOf(
                        "date" to PropertySchema("string", "날짜 (YYYY-MM-DD 형식)")
                    ),
                    required = listOf("date")
                )
            )
        )

        return generateContent(prompt, tools)
    }
}

// Gemini API 요청/응답 모델들
@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiRequest(
    val contents: List<Content>,
    val tools: List<Tool>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Content(
    val parts: List<Part>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Part(
    val text: String? = null,
    val functionCall: FunctionCall? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tool(
    @JsonProperty("function_declarations")
    val functionDeclarations: List<FunctionDeclaration>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: FunctionParameters
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FunctionParameters(
    val type: String,
    val properties: Map<String, PropertySchema>,
    val required: List<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PropertySchema(
    val type: String,
    val description: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FunctionCall(
    val name: String,
    val args: Map<String, Any>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val error: ErrorInfo? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorInfo(
    val code: Int,
    val message: String,
    val status: String
)
