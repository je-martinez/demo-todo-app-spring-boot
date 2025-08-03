package com.je_martinez.demo.database.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class User (
    @Indexed(unique = true, name = "email_uq_idx")
    val email: String,

    val hashedPassword: String,

    @Id val id: ObjectId = ObjectId()
)