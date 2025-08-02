package com.ittae.be.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl("http://mcp-server.com/api").build() // MCP 서버의 기본 URL로 변경해야 합니다.
    }
}
