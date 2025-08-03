package com.je_martinez.demo.features.authentication.services

import com.je_martinez.demo.database.models.RefreshToken
import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.je_martinez.demo.features.authentication.exceptions.AuthExceptions
import com.je_martinez.demo.features.authentication.exceptions.TokenExceptions
import com.je_martinez.demo.features.authentication.utils.HashEncoder
import com.je_martinez.demo.features.authentication.utils.TokenUtils
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class RefreshTokenService(
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String){
        val hashed = TokenUtils.hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)
       refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }
}