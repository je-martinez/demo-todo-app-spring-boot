package com.je_martinez.demo.cache.features.todos

import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object TodosCacheSettings {
    const val FEATURE_NAME = "Todos"
    const val COUNT_KEY = "$FEATURE_NAME:Count"
    const val FIND_ALL_KEY = "$FEATURE_NAME:All"
    const val FIND_BY_OWNER_KEY = "$FEATURE_NAME:ByOwnerId"
    const val FIND_BY_ID_KEY = "$FEATURE_NAME:ById"

    val CACHE_TTL = mapOf(
        COUNT_KEY to 5.minutes.toJavaDuration(),
        FIND_ALL_KEY to 5.minutes.toJavaDuration(),
        FIND_BY_OWNER_KEY to 5.minutes.toJavaDuration(),
        FIND_BY_ID_KEY to 10.minutes.toJavaDuration()
    )

}