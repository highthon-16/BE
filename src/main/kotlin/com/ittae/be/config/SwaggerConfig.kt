package com.ittae.be.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Ittae API")
                    .description("Ittae API 명세서")
                    .version("v1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes("JWT", securityScheme())
            )
            .addSecurityItem(SecurityRequirement().addList("JWT"))
    }

    private fun securityScheme(): SecurityScheme {
        return SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .description("JWT Authorization header using the Bearer scheme. Example: 'Authorization: Bearer {token}'")
    }
}