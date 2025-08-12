package com.je_martinez.demo.database.models

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "RefreshTokens")
@CompoundIndex(def = "{'userId': 1, 'hashedToken': 1}", name = "userId_hashToken_idx")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s", name = "expiresAt_ttl_idx")
    val expiresAt: Instant,
    val hashedToken: String,
    val createdAt : Instant = Instant.now()
)