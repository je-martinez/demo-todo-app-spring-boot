package com.je_martinez.demo.database.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Document(collection = "Todos")
@CompoundIndexes(
    CompoundIndex(
        name = "id_ownerId_idx",
        def = """{"_id": 1, "ownerId": 1}""",
    ),
    CompoundIndex(
        name = "ownerId_createdAt_idx",
        def = """{"ownerId": 1, "createdAt": 1}"""
    )
)
data class Todo(
    val title: String = "",
    val description: String = "",
    @Indexed(name = "date_idx") val date: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    @Indexed(name = "ownerId_idx") val ownerId: ObjectId,
    @Indexed(name = "createdAt_idx") val createdAt: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val completedAt: OffsetDateTime? = null,
    @Indexed(name = "completed_idx") val completed: Boolean = false,
    val cover: CoverImage = CoverImage(uri = null, state = CoverImageState.PROVISIONING),
    @Id val id: ObjectId = ObjectId()
)