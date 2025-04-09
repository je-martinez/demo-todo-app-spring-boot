package com.je_martinez.demo.security

import com.je_martinez.demo.exceptions.TokenExceptions
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

enum class TokenType {
    TOKEN, REFRESH_TOKEN
}

@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
) {

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
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    private fun parseAllClaims(token: String): Claims?{
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        }else token

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

    fun generateAccessToken(userId: String):String{
        return generateToken(userId, TokenType.TOKEN, accessTokenValidityMs)
    }

    fun generateRefreshToken(userId: String):String{
        return generateToken(userId, TokenType.REFRESH_TOKEN, refreshTokenValidityMs)
    }

    private fun validateToken(userId:String, tokenType:TokenType): Boolean{
        val claims = parseAllClaims(userId) ?: return false
        val claimTokenType = claims["type"] as? TokenType ?: return false
        return claimTokenType == tokenType
    }

    fun validateAccessToken(userId: String):Boolean{
        return validateToken(userId, TokenType.TOKEN)
    }

    fun validateRefreshToken(userId: String):Boolean{
        return validateToken(userId, TokenType.REFRESH_TOKEN)
    }

    fun getUserIdFromToken(token:String):String{
        val claims = parseAllClaims(token) ?: throw TokenExceptions.invalidToken()
        return claims.subject
    }

}