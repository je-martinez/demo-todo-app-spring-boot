package com.je_martinez.demo.features.todos.dtos.responses

import com.je_martinez.demo.database.models.Todo
import java.time.Instant
import java.time.OffsetDateTime

data class TodoResponse(
    val id: String,
    val title: String,
    val ownerId: String,
    val description: String,
    val date: OffsetDateTime,
    val cover: CoverImageResponse,
    val createdAt: OffsetDateTime,
    val completedAt: OffsetDateTime?,
    val completed: Boolean
)

fun Todo.toResponse(): TodoResponse {
    return TodoResponse(
        id = this.id.toHexString(),
        title = this.title,
        description = this.description,
        date =  this.date,
        ownerId = this.ownerId.toHexString(),
        cover = this.cover.toResponse(),
        createdAt = this.createdAt,
        completedAt = this.completedAt,
        completed = this.completed,
    )
}