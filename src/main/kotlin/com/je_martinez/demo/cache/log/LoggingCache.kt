package com.je_martinez.demo.cache.log

import org.slf4j.Logger
import org.springframework.cache.Cache
import org.springframework.cache.interceptor.SimpleKey
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class LoggingCache(
    private val delegate: Cache,
    private val log: Logger
): Cache by delegate{

    override fun get(key: Any): Cache.ValueWrapper? {
        val value = delegate.get(key)
        val displayKey = formatKey(key)
        if (value != null) {
            log.info("✅ Cache HIT for [${delegate.name}] with key $displayKey")
        }
        return value
    }

    override fun retrieve(key: Any): CompletableFuture<*>? {
        return delegate.retrieve(key)
    }

    override fun <T : Any?> retrieve(
        key: Any,
        valueLoader: Supplier<CompletableFuture<T?>?>
    ): CompletableFuture<T?> {
        return delegate.retrieve(key, valueLoader)
    }

    override fun put(key: Any, value: Any?) {
        val displayKey = formatKey(key)
        log.info("📝 Cache PUT on [${delegate.name}] for key $displayKey with value: $value")
        delegate.put(key, value)
    }

    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? {
        val displayKey = formatKey(key)
        log.info("📌 Cache PUT_IF_ABSENT on [${delegate.name}] for key $displayKey")
        return delegate.putIfAbsent(key, value)
    }

    override fun evict(key: Any) {
        val displayKey = formatKey(key)
        log.info("🗑️ Cache EVICT on [${delegate.name}] for key $displayKey")
        delegate.evict(key)
    }

    override fun evictIfPresent(key: Any): Boolean {
        return delegate.evictIfPresent(key)
    }

    override fun clear() {
        log.info("🔥 Cache CLEAR on [${delegate.name}]")
        delegate.clear()
    }

    override fun invalidate(): Boolean {
        return delegate.invalidate()
    }

    private fun formatKey(key: Any?): String {
        return when (key) {
            null -> "<null>"
            is SimpleKey -> {
                if (key == SimpleKey.EMPTY) "<default>" else key.toString()
            }
            else -> key.toString()
        }
    }
}