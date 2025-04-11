package com.je_martinez.demo.security

import com.je_martinez.demo.exceptions.TokenExceptions
import com.je_martinez.demo.utils.JwtUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
) {

    enum class TokenType {
        TOKEN, REFRESH_TOKEN
    }

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60L * 1000L
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L

    private fun generateToken(
        userId: String,
        type: TokenType,
        expiry: Long,
    ):String{
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return JwtUtils.generateToken(
            secretKey = secretKey,
            subject = userId,
            issuedAt = now,
            expiration = expiryDate,
            claims = mapOf(
                "type" to type
            ),
        )
    }

    private fun parseAllClaims(token: String): Claims?{
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        }else token

        return JwtUtils.extractPayload(secretKey, rawToken)
    }

    fun generateAccessToken(userId: String):String{
        return generateToken(userId, TokenType.TOKEN, accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String):String{
        return generateToken(userId, TokenType.REFRESH_TOKEN, refreshTokenValidityMs)
    }

    private fun validateToken(userId:String, tokenType:String): Boolean{
        val claims = parseAllClaims(userId) ?: return false
        val claimTokenType = claims["type"] as? String ?: return false
        return claimTokenType == tokenType
    }

    fun validateAccessToken(userId: String):Boolean{
        return validateToken(userId, TokenType.TOKEN.toString())
    }

    fun validateRefreshToken(userId: String):Boolean{
        return validateToken(userId, TokenType.REFRESH_TOKEN.toString())
    }

    fun getUserIdFromToken(token:String):String{
        val claims = parseAllClaims(token) ?: throw TokenExceptions.invalidToken()
        return claims.subject
    }

}