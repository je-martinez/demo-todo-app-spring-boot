package com.je_martinez.demo.database.config

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

/**
 * Custom codec for OffsetDateTime to handle MongoDB serialization/deserialization
 * Stores as MongoDB Date type for proper date operations and indexing
 */
class OffsetDateTimeCodec : Codec<OffsetDateTime> {
    
    override fun encode(writer: BsonWriter, value: OffsetDateTime?, encoderContext: EncoderContext) {
        if (value == null) {
            writer.writeNull()
        } else {
            // Convert OffsetDateTime to Date (MongoDB's native date type)
            val date = Date.from(value.toInstant())
            writer.writeDateTime(date.time)
        }
    }
    
    override fun decode(reader: BsonReader, decoderContext: DecoderContext): OffsetDateTime? {
        return when (reader.currentBsonType) {
            org.bson.BsonType.NULL -> {
                reader.readNull()
                null
            }
            org.bson.BsonType.DATE_TIME -> {
                val date = Date(reader.readDateTime())
                // Convert Date back to OffsetDateTime, preserving the original offset
                // Note: We lose the original offset information, so we use UTC as default
                // If you need to preserve specific offsets, consider storing offset separately
                date.toInstant().atOffset(ZoneOffset.UTC)
            }
            else -> throw IllegalArgumentException("Cannot decode ${reader.currentBsonType} to OffsetDateTime")
        }
    }
    
    override fun getEncoderClass(): Class<OffsetDateTime> = OffsetDateTime::class.java
}

/**
 * Codec provider for OffsetDateTime
 */
class OffsetDateTimeCodecProvider : CodecProvider {
    
    override fun <T> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        return if (clazz == OffsetDateTime::class.java) {
            @Suppress("UNCHECKED_CAST")
            OffsetDateTimeCodec() as Codec<T>
        } else {
            null
        }
    }
}
