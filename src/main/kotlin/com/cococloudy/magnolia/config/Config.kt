package com.cococloudy.magnolia.config

import com.querydsl.jpa.impl.JPAQueryFactory
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class Config {

    @PostConstruct
    fun setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Bean
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Configuration
    class QueryDslConfiguration(
        @PersistenceContext
        val entityManager: EntityManager
    ) {
        @Bean
        fun jpaQueryFactory() = JPAQueryFactory(entityManager)
    }

    private val authorization = "Authorization"

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Magnolia API")
            )
            .components(
                Components()
                    .addSecuritySchemes(authorization, bearerSecuritySchema())
            )
            .security(Collections.singletonList(SecurityRequirement().addList(authorization)))
    }

    fun bearerSecuritySchema(): SecurityScheme {
        return SecurityScheme()
            .name(authorization)
            .description("Description about the TOKEN")
            .scheme("bearer")
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
    }
}