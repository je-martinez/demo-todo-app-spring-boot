package com.je_martinez.demo.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.util.Date
import javax.crypto.SecretKey

object JwtUtils {
    fun generateToken(
        secretKey: SecretKey,
        subject: String,
        issuedAt: Date,
        expiration: Date,
        claims: Map<String, Any> = emptyMap(),
    ):String {
        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun extractPayload(
        secretKey: SecretKey,
        rawToken: String,
    ): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        }catch (e: Exception){
            return null
        }
    }
}