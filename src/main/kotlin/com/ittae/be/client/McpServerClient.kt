package com.ittae.be.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class McpServerClient(
    private val webClient: WebClient,
    @Value("\${mcp.server.url:https://mcp.ittae.kro.kr}") private val mcpServerUrl: String
) {

    fun callMcpFunction(functionName: String, args: Map<String, Any>): Mono<McpResponse> {
        val request = McpRequest(
            method = "tools/call",
            params = McpParams(
                name = functionName,
                arguments = args
            )
        )

        return webClient.post()
            .uri("$mcpServerUrl/mcp")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(McpResponse::class.java)
            .doOnError { error ->
                println("MCP 서버 호출 오류: ${error.message}")
            }
    }

    fun listTools(): Mono<McpResponse> {
        val request = McpRequest(
            method = "tools/list",
            params = McpParams(
                name = "",
                arguments = emptyMap()
            )
        )

        return webClient.post()
            .uri("$mcpServerUrl/tools/list")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(McpResponse::class.java)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class McpRequest(
    val method: String,
    val params: McpParams
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class McpParams(
    val name: String,
    val arguments: Map<String, Any>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class McpResponse(
    val result: Any? = null,
    val error: McpError? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class McpError(
    val code: Int,
    val message: String
)
