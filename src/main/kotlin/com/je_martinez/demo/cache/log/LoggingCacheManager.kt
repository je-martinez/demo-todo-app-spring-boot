package com.je_martinez.demo.cache.log

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

class LoggingCacheManager(
    private val delegate: CacheManager
): CacheManager{
    private val log = LoggerFactory.getLogger(LoggingCacheManager::class.java)
    override fun getCache(name: String): Cache? {
        val cache = delegate.getCache(name)
        return cache?.let { LoggingCache(it, log) }
    }
    override fun getCacheNames(): Collection<String?> = delegate.cacheNames
}