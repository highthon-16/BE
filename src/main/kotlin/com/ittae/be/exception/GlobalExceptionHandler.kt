package com.ittae.be.exception

import com.ittae.be.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException occurred: {}", ex.message, ex)
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = ex.message ?: "잘못된 요청입니다.",
                code = "INVALID_ARGUMENT"
            )
        )
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalStateException occurred: {}", ex.message, ex)
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = ex.message ?: "잘못된 상태입니다.",
                code = "INVALID_STATE"
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { error ->
            when (error) {
                is FieldError -> "${error.field}: ${error.defaultMessage}"
                else -> error.defaultMessage ?: "유효하지 않은 값입니다."
            }
        }
        
        logger.warn("Validation failed: {}", errors, ex)
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = "입력값 검증에 실패했습니다: $errors",
                code = "VALIDATION_FAILED"
            )
        )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        logger.warn("NoSuchElementException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ex.message ?: "요청한 리소스를 찾을 수 없습니다.",
                code = "RESOURCE_NOT_FOUND"
            )
        )
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("UserNotFoundException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ex.message ?: "사용자를 찾을 수 없습니다.",
                code = "USER_NOT_FOUND"
            )
        )
    }

    @ExceptionHandler(EventNotFoundException::class)
    fun handleEventNotFoundException(ex: EventNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("EventNotFoundException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ex.message ?: "이벤트를 찾을 수 없습니다.",
                code = "EVENT_NOT_FOUND"
            )
        )
    }

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmailException(ex: DuplicateEmailException): ResponseEntity<ErrorResponse> {
        logger.warn("DuplicateEmailException occurred: {}", ex.message, ex)
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = ex.message ?: "이미 사용 중인 이메일입니다.",
                code = "DUPLICATE_EMAIL"
            )
        )
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ErrorResponse> {
        logger.warn("InvalidTokenException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                message = ex.message ?: "유효하지 않은 토큰입니다.",
                code = "INVALID_TOKEN"
            )
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        logger.warn("AccessDeniedException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                message = "접근 권한이 없습니다.",
                code = "ACCESS_DENIED"
            )
        )
    }
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected RuntimeException occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                message = "서버 내부 오류가 발생했습니다.",
                code = "INTERNAL_SERVER_ERROR"
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected Exception occurred: {}", ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                message = "알 수 없는 오류가 발생했습니다.",
                code = "UNKNOWN_ERROR"
            )
        )
    }
}

// 커스텀 예외 클래스들
class UserNotFoundException(message: String) : RuntimeException(message)
class EventNotFoundException(message: String) : RuntimeException(message)
class DuplicateEmailException(message: String) : RuntimeException(message)
class InvalidTokenException(message: String) : RuntimeException(message)
class AccessDeniedException(message: String) : RuntimeException(message)
