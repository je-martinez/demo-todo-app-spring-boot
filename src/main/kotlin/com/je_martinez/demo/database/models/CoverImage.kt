package com.je_martinez.demo.database.models

import org.springframework.data.mongodb.core.mapping.Field

data class CoverImage(
    @Field(write = Field.Write.ALWAYS)
    val uri: String? = null,
    val thumbnailUri: String? = null,
    val blurhash: String? = null,
    val state: CoverImageState
)