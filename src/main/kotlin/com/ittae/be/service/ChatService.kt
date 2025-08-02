package com.ittae.be.service

import com.ittae.be.client.GeminiClient
import com.ittae.be.client.McpServerClient
import com.ittae.be.client.FunctionCall
import com.ittae.be.dto.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ChatService(
    private val geminiClient: GeminiClient,
    private val mcpServerClient: McpServerClient,
    private val calendarService: CalendarService
) {

    private val logger = LoggerFactory.getLogger(ChatService::class.java)

    fun processChat(request: ChatRequest, userId: Long): Mono<ChatResponse> {
        val sessionId = request.sessionId ?: generateSessionId()
        
        logger.info("Processing chat request for user: $userId, session: $sessionId")
        logger.info("User message: \${request.message}")
        
        // 현재 시간 정보 추가
        val contextualPrompt = buildContextualPrompt(request.message, userId)
        
        return geminiClient.generateContentWithCalendarTools(contextualPrompt)
            .flatMap { geminiResponse ->
                handleGeminiResponse(geminiResponse, userId, sessionId)
            }
            .onErrorResume { error ->
                logger.error("Chat processing error: ${error.message}", error)
                val errorMessage = when {
                    error.message?.contains("429") == true || error.message?.contains("Too Many Requests") == true -> 
                        "API 요청 한도가 초과되었습니다. 잠시 후 다시 시도해주세요."
                    error.message?.contains("401") == true || error.message?.contains("Unauthorized") == true -> 
                        "API 키가 유효하지 않습니다. 관리자에게 문의해주세요."
                    else -> "죄송합니다. 요청을 처리하는 중 오류가 발생했습니다."
                }
                Mono.just(createErrorResponse(sessionId, errorMessage))
            }
    }

    private fun buildContextualPrompt(message: String, userId: Long): String {
        val currentTime = LocalDateTime.now()
        return """
            현재 시간: \${currentTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}
            사용자 ID: $userId
            
            사용자 요청: $message
            
            다음 상황에 맞게 적절한 함수를 호출하거나 응답해 주세요:
            1. 일정 생성 요청 시 -> create_calendar_event 함수 호출
            2. 일정 조회 요청 시 -> get_all_events 또는 get_events_by_date 함수 호출
            3. 일정 수정 요청 시 -> update_calendar_event 함수 호출
            4. 일정 삭제 요청 시 -> delete_calendar_event 함수 호출
            5. 일반 대화 시 -> 친근하고 도움이 되는 응답
            
            주의사항:
            - 시간은 ISO 형식(YYYY-MM-DDTHH:mm:ss)을 사용하세요
            - 카테고리는 STUDY, WORK, REST, ACTIVITY 중 하나를 사용하세요
            - 스태미나 소모량은 1-100 사이의 값을 사용하세요
        """.trimIndent()
    }

    private fun handleGeminiResponse(geminiResponse: com.ittae.be.client.GeminiResponse, userId: Long, sessionId: String): Mono<ChatResponse> {
        if (geminiResponse.error != null) {
            logger.error("Gemini API error: \${geminiResponse.error}")
            return Mono.just(createErrorResponse(sessionId, "AI 처리 중 오류가 발생했습니다: \${geminiResponse.error.message}"))
        }

        val candidate = geminiResponse.candidates?.firstOrNull()
        if (candidate?.content?.parts?.isEmpty() != false) {
            return Mono.just(createErrorResponse(sessionId, "응답을 생성할 수 없습니다."))
        }

        val parts = candidate.content.parts
        val functionCallPart = parts.find { it.functionCall != null }
        
        return if (functionCallPart?.functionCall != null) {
            // 함수 호출이 있는 경우
            val functionCall = functionCallPart.functionCall
            logger.info("Executing function: \${functionCall.name} with args: \${functionCall.args}")
            
            executeFunctionCall(functionCall, userId)
                .map { actionResult ->
                    val textParts = parts.filter { it.text != null }
                    val aiMessage = textParts.joinToString(" ") { it.text ?: "" }.ifEmpty { 
                        "작업을 완료했습니다." 
                    }
                    
                    ChatResponse(
                        message = aiMessage,
                        sessionId = sessionId,
                        actionPerformed = actionResult,
                        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                }
        } else {
            // 일반 텍스트 응답
            val textResponse = parts.filter { it.text != null }
                .joinToString(" ") { it.text ?: "" }
                .ifEmpty { "죄송합니다. 응답을 생성할 수 없습니다." }
            
            Mono.just(ChatResponse(
                message = textResponse,
                sessionId = sessionId,
                actionPerformed = null,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ))
        }
    }

    private fun executeFunctionCall(functionCall: FunctionCall, userId: Long): Mono<ActionInfo> {
        return when (functionCall.name) {
            "create_calendar_event" -> {
                val args = functionCall.args
                val eventRequest = CalendarEventRequest(
                    title = args["title"]?.toString() ?: "제목 없음",
                    startTime = args["start_time"]?.toString() ?: LocalDateTime.now().toString(),
                    duration = (args["duration"] as? Number)?.toInt() ?: 60,
                    category = args["category"]?.toString() ?: "WORK",
                    description = args["description"]?.toString(),
                    location = args["location"]?.toString(),
                    staminaCost = (args["stamina_cost"] as? Number)?.toInt()
                )
                
                calendarService.createEvent(eventRequest, userId)
                    .map { eventResponse ->
                        ActionInfo(
                            actionType = "CREATE_EVENT",
                            result = eventResponse,
                            success = true
                        )
                    }
                    .onErrorReturn(ActionInfo(
                        actionType = "CREATE_EVENT",
                        result = "일정 생성에 실패했습니다.",
                        success = false
                    ))
            }
            
            "get_all_events" -> {
                calendarService.getAllEvents(userId)
                    .map { events ->
                        ActionInfo(
                            actionType = "GET_ALL_EVENTS",
                            result = events,
                            success = true
                        )
                    }
                    .onErrorReturn(ActionInfo(
                        actionType = "GET_ALL_EVENTS",
                        result = "일정 조회에 실패했습니다.",
                        success = false
                    ))
            }
            
            "get_events_by_date" -> {
                val date = functionCall.args["date"]?.toString() ?: LocalDateTime.now().toLocalDate().toString()
                calendarService.getEventsByDate(date, userId)
                    .map { events ->
                        ActionInfo(
                            actionType = "GET_EVENTS_BY_DATE",
                            result = events,
                            success = true
                        )
                    }
                    .onErrorReturn(ActionInfo(
                        actionType = "GET_EVENTS_BY_DATE",
                        result = "일정 조회에 실패했습니다.",
                        success = false
                    ))
            }
            
            "update_calendar_event" -> {
                val args = functionCall.args
                val eventId = (args["event_id"] as? Number)?.toLong() ?: return Mono.just(
                    ActionInfo("UPDATE_EVENT", "이벤트 ID가 필요합니다.", false)
                )
                
                val eventRequest = CalendarEventRequest(
                    title = args["title"]?.toString() ?: "제목 없음",
                    startTime = args["start_time"]?.toString() ?: LocalDateTime.now().toString(),
                    duration = (args["duration"] as? Number)?.toInt() ?: 60,
                    category = args["category"]?.toString() ?: "WORK",
                    description = args["description"]?.toString(),
                    location = args["location"]?.toString(),
                    staminaCost = (args["stamina_cost"] as? Number)?.toInt()
                )
                
                calendarService.updateEvent(eventId, eventRequest, userId)
                    .map { eventResponse ->
                        ActionInfo(
                            actionType = "UPDATE_EVENT",
                            result = eventResponse,
                            success = true
                        )
                    }
                    .onErrorReturn(ActionInfo(
                        actionType = "UPDATE_EVENT",
                        result = "일정 수정에 실패했습니다.",
                        success = false
                    ))
            }
            
            "delete_calendar_event" -> {
                val eventId = (functionCall.args["event_id"] as? Number)?.toLong() ?: return Mono.just(
                    ActionInfo("DELETE_EVENT", "이벤트 ID가 필요합니다.", false)
                )
                
                calendarService.deleteEvent(eventId, userId)
                    .then(Mono.just(ActionInfo(
                        actionType = "DELETE_EVENT",
                        result = "일정이 삭제되었습니다.",
                        success = true
                    )))
                    .onErrorReturn(ActionInfo(
                        actionType = "DELETE_EVENT",
                        result = "일정 삭제에 실패했습니다.",
                        success = false
                    ))
            }
            
            else -> {
                logger.warn("Unknown function call: \${functionCall.name}")
                Mono.just(ActionInfo(
                    actionType = "UNKNOWN",
                    result = "알 수 없는 기능입니다.",
                    success = false
                ))
            }
        }
    }

    private fun createErrorResponse(sessionId: String, message: String): ChatResponse {
        return ChatResponse(
            message = message,
            sessionId = sessionId,
            actionPerformed = null,
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }

    private fun generateSessionId(): String {
        return "chat-\${UUID.randomUUID().toString().substring(0, 8)}"
    }
}
