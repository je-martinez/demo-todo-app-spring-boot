package com.je_martinez.demo.features.todos.dtos.responses

import com.je_martinez.demo.database.models.Todo
import java.time.Instant

data class TodoResponse(
    val id: String,
    val title: String,
    val ownerId: String,
    val description: String,
    val cover: CoverImageResponse,
    val createdAt: Instant,
    val completedAt: Instant?,
    val completed: Boolean
)

fun Todo.toResponse(): TodoResponse {
    return TodoResponse(
        id = this.id.toHexString(),
        title = this.title,
        description = this.description,
        ownerId = this.ownerId.toHexString(),
        cover = this.cover.toResponse(),
        createdAt = this.createdAt,
        completedAt = this.completedAt,
        completed = this.completed,
    )
}