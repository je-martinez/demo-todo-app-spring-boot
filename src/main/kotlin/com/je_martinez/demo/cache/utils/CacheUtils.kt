package com.je_martinez.demo.cache.utils
import org.springframework.data.redis.cache.RedisCacheConfiguration
import java.time.Duration

object CacheUtils {
    fun buildFeatureMap(config: RedisCacheConfiguration, featureMap: Map<String, Duration>): Map<String, RedisCacheConfiguration>{
        return featureMap.map {
            it.key to config.entryTtl(it.value)
        }.toMap()
    }
}