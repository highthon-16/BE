package com.ittae.be.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GeminiConfig {
    
    @Bean("geminiWebClient")
    fun geminiWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build()
    }
}
