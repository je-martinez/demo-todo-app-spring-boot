package com.je_martinez.demo.database.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig(
    private val env: Environment // Constructor injection for Environment
) : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        // Try to get the DB name from "spring.data.mongodb.database"
        val dbFromProp = env.getProperty("spring.data.mongodb.database")
        if (!dbFromProp.isNullOrBlank()) return dbFromProp

        // If the database name is not explicitly set, parse it from the URI
        val uri = env.getRequiredProperty("spring.data.mongodb.uri")
        val cs = com.mongodb.ConnectionString(uri)
        return cs.database ?: throw IllegalStateException(
            "The URI does not include the database name and 'spring.data.mongodb.database' was not set"
        )
    }

    override fun mongoClient(): MongoClient {
        // Explicitly create the MongoClient using the URI from environment variables
        val uri = env.getRequiredProperty("spring.data.mongodb.uri")
        return MongoClients.create(uri)
    }

    override fun autoIndexCreation(): Boolean = true // Enable automatic index creation
}
