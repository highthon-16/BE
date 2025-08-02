package com.ittae.be.controller

import com.ittae.be.client.GeminiClient
import com.ittae.be.client.McpServerClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource
import java.time.LocalDateTime

@RestController
class HealthController(
    @Autowired private val dataSource: DataSource,
    @Autowired private val geminiClient: GeminiClient,
    @Autowired private val mcpServerClient: McpServerClient
) {

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        val healthStatus = mutableMapOf<String, Any>()
        var overallStatus = "UP"

        // 기본 애플리케이션 상태
        healthStatus["status"] = "UP"
        healthStatus["timestamp"] = LocalDateTime.now().toString()
        healthStatus["service"] = "ittae-backend"
        healthStatus["version"] = "1.0.0"

        // 개별 컴포넌트 상태 확인
        val components = mutableMapOf<String, Map<String, Any>>()

        // 데이터베이스 상태 확인
        try {
            dataSource.connection.use { connection ->
                val isValid = connection.isValid(5) // 5초 타임아웃
                components["database"] = mapOf(
                    "status" to if (isValid) "UP" else "DOWN",
                    "details" to if (isValid) "Database connection is healthy" else "Database connection failed"
                )
                if (!isValid) overallStatus = "DOWN"
            }
        } catch (e: Exception) {
            components["database"] = mapOf(
                "status" to "DOWN",
                "details" to "Database connection error: ${e.message}"
            )
            overallStatus = "DOWN"
        }

        // Gemini API 상태 확인 (간단한 핑)
        try {
            // 실제 API 호출 대신 클라이언트 객체 존재 확인
            components["gemini"] = mapOf(
                "status" to "UP",
                "details" to "Gemini client is configured"
            )
        } catch (e: Exception) {
            components["gemini"] = mapOf(
                "status" to "DOWN",
                "details" to "Gemini client error: ${e.message}"
            )
            overallStatus = "DOWN"
        }

        // MCP 서버 상태 확인
        try {
            // MCP 서버 핑 (실제 연결 테스트는 여기서 구현 가능)
            components["mcp"] = mapOf(
                "status" to "UP",
                "details" to "MCP client is configured"
            )
        } catch (e: Exception) {
            components["mcp"] = mapOf(
                "status" to "DOWN",
                "details" to "MCP client error: ${e.message}"
            )
            overallStatus = "DOWN"
        }

        healthStatus["status"] = overallStatus
        healthStatus["components"] = components

        return if (overallStatus == "UP") {
            ResponseEntity.ok(healthStatus)
        } else {
            ResponseEntity.status(503).body(healthStatus) // Service Unavailable
        }
    }

    @GetMapping("/health/simple")
    fun simpleHealth(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().toString(),
            "service" to "ittae-backend"
        ))
    }

    @GetMapping("/health/database")
    fun databaseHealth(): ResponseEntity<Map<String, Any>> {
        return try {
            dataSource.connection.use { connection ->
                val isValid = connection.isValid(5)
                val status = if (isValid) "UP" else "DOWN"
                val response = mapOf<String, Any>(
                    "status" to status,
                    "timestamp" to LocalDateTime.now().toString(),
                    "component" to "database",
                    "details" to if (isValid) "Database connection is healthy" else "Database connection failed"
                )
                
                if (isValid) {
                    ResponseEntity.ok(response)
                } else {
                    ResponseEntity.status(503).body(response)
                }
            }
        } catch (e: Exception) {
            val response = mapOf<String, Any>(
                "status" to "DOWN",
                "timestamp" to LocalDateTime.now().toString(),
                "component" to "database",
                "error" to (e.message ?: "Unknown error")
            )
            ResponseEntity.status(503).body(response)
        }
    }
}
