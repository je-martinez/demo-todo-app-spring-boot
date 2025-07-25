package com.je_martinez.demo.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Configuration
class RedisConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndRegisterModules()
    }

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        }
    }

    @Bean
    fun cacheConfiguration(mapper: ObjectMapper): RedisCacheConfiguration {
        val myMapper = mapper.copy()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .activateDefaultTyping(
                jacksonObjectMapper().polymorphicTypeValidator,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
            )
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(5.minutes.toJavaDuration())
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(myMapper)
                )
            )
    }
}