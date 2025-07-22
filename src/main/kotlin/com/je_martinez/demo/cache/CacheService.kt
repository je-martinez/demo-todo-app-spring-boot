package com.je_martinez.demo.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class CacheService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {
    fun save(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun find(key: String): String? {
        return redisTemplate.opsForValue().get(key) as String?
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> findAsType(key: String, typeRef: TypeReference<R>): R? {
        val raw = redisTemplate.opsForValue().get(key)
        return when (raw) {
            is String -> objectMapper.readValue(raw, typeRef)
            else -> raw as? R
        }
    }

    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}