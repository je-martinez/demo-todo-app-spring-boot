package com.je_martinez.demo.dtos.todos

import java.time.Instant

data class TodoResponse(
    val id: String,
    val title: String,
    val ownerId: String,
    val description: String,
    val createdAt: Instant,
    val completedAt: Instant?,
    val completed: Boolean
)