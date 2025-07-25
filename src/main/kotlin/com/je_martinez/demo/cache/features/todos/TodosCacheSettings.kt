package com.je_martinez.demo.cache.features.todos

object TodosCacheSettings {
    const val FEATURE_NAME = "Todos"
    const val COUNT_KEY = "Count"
    const val FIND_ALL_KEY = "'*'"
    const val FIND_BY_OWNER_KEY = "'OwnerId:' + #ownerId"
    const val FIND_BY_ID_KEY = "'Id:' + #id"
}