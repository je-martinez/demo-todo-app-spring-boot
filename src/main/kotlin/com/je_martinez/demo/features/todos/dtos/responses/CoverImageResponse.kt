package com.je_martinez.demo.features.todos.dtos.responses

import com.je_martinez.demo.database.models.CoverImage
import com.je_martinez.demo.database.models.CoverImageState

data class CoverImageResponse(
    val uri: String? = null,
    val thumbnailUri: String? = null,
    val blurhash: String? = null,
    val state: CoverImageState
)

fun CoverImage.toResponse(): CoverImageResponse =
    CoverImageResponse(
        uri = this.uri,
        thumbnailUri = this.thumbnailUri,
        blurhash = this.blurhash,
        state = this.state
    )