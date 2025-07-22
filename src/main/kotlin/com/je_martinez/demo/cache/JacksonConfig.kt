package com.je_martinez.demo.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule()) // Enables LocalDate, Instant, etc.
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
}
