package com.je_martinez.demo.database.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "Todos")
data class Todo(
    val title: String = "",
    val description: String = "",
    @Indexed
    val ownerId: ObjectId,
    @Indexed
    val createdAt: Instant = Instant.now(),
    val completedAt: Instant? = null,
    val completed: Boolean = false,
    @Id val id: ObjectId = ObjectId(),
)