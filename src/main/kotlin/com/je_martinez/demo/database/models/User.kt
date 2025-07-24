package com.je_martinez.demo.database.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class User (

    val email: String,
    val hashedPassword: String,
    @Id val id: ObjectId = ObjectId()
)