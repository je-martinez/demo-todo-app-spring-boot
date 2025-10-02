package com.je_martinez.demo.database.config

import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

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
        val settings = MongoClientSettings.builder()
            .applyConnectionString(com.mongodb.ConnectionString(uri))
            .codecRegistry(codecRegistry())
            .build()
        return MongoClients.create(settings)
    }

    override fun autoIndexCreation(): Boolean = true // Enable automatic index creation

    @Bean
    fun codecRegistry(): CodecRegistry {
        return CodecRegistries.fromRegistries(
            CodecRegistries.fromProviders(OffsetDateTimeCodecProvider()),
            MongoClientSettings.getDefaultCodecRegistry()
        )
    }

    @Bean
    override fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                OffsetDateTimeToDateConverter(),
                DateToOffsetDateTimeConverter()
            )
        )
    }

    /**
     * Converter from OffsetDateTime to Date for Spring Data MongoDB
     */
    class OffsetDateTimeToDateConverter : org.springframework.core.convert.converter.Converter<OffsetDateTime, Date> {
        override fun convert(source: OffsetDateTime): Date {
            return Date.from(source.toInstant())
        }
    }

    /**
     * Converter from Date to OffsetDateTime for Spring Data MongoDB
     */
    class DateToOffsetDateTimeConverter : org.springframework.core.convert.converter.Converter<Date, OffsetDateTime> {
        override fun convert(source: Date): OffsetDateTime {
            return source.toInstant().atOffset(ZoneOffset.UTC)
        }
    }
}
