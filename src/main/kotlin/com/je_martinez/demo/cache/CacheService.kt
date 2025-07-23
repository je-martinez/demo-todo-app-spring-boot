package com.je_martinez.demo.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.je_martinez.demo.utils.LoggerUtils
import com.je_martinez.demo.utils.LoggerUtils.logger
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Repository
class CacheService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {

    private val log by LoggerUtils.logger()

    fun save(key: String, value: Any, ttl: Duration = 30.seconds.toJavaDuration()) {
        log.info("Saving key: $key on Redis Cache")
        redisTemplate.opsForValue().set(key, value, ttl)
    }

    fun find(key: String): String? {
        log.info("Retrieving key: $key from Redis Cache")
        val cache = redisTemplate.opsForValue().get(key) as String?
        if(cache == null){
            log.info("Key: $key not found on Redis Cache")
        }
        return cache
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> findAsType(key: String, typeRef: TypeReference<R>): R? {
        log.info("Retrieving key:<$key> with type:<$typeRef> from Redis Cache")
        val raw = redisTemplate.opsForValue().get(key)
        if(raw == null){
            log.info("Key:<$key> with type:<$typeRef> not found on Redis Cache")
        }
        return when (raw) {
            is String -> objectMapper.readValue(raw, typeRef)
            else -> raw as? R
        }
    }

    fun delete(key: String): Boolean {
        log.info("Deleting key:<$key> on Redis Cache")
        return redisTemplate.delete(key)
    }
}