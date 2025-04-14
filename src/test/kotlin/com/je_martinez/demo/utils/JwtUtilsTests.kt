package com.je_martinez.demo.utils

import com.je_martinez.demo.features.authentication.JwtService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.assertEquals

class JwtUtilsTests {

    private val keyAsString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean enim erat, accumsan id lacus rhoncus."
    private val secretKey =  Keys.hmacShaKeyFor(keyAsString.encodeToByteArray())
    private val subject = ObjectId().toHexString()
    private val issuedAt = Date.from(Instant.now())
    private val expiresAt = Date.from(Instant.now().plus(30, ChronoUnit.DAYS))
    private val claims = mapOf(
        "other" to "other"
    )

    @Test
    fun `Should be able to generate token`() {

        val result = JwtUtils.generateToken(
            secretKey = secretKey,
            subject = subject,
            issuedAt = issuedAt,
            expiration = expiresAt,
            claims = claims
        )

        assertInstanceOf<String>(result)
    }

    @Test
    fun `Should be able to read claims from a valid token`() {

        val newClaims = claims.toMutableMap()
        newClaims["type"] = JwtService.TokenType.TOKEN.toString()

        val result = JwtUtils.generateToken(
            secretKey = secretKey,
            subject = subject,
            issuedAt = issuedAt,
            expiration = expiresAt,
        )

        val myClaims = JwtUtils.extractPayload(secretKey, result)
        assertInstanceOf<Claims>(result)
        assertEquals(myClaims!!["type"], JwtService.TokenType.TOKEN.toString())
        assertEquals(myClaims["other"], "other")
        assertEquals(myClaims.subject, subject)
    }
}