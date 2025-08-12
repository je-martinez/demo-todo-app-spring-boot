package com.je_martinez.demo.features.authentication.services

import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.dtos.responses.RegisterResponse
import com.je_martinez.demo.features.authentication.dtos.responses.toResponse
import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.je_martinez.demo.features.authentication.exceptions.AuthExceptions
import com.je_martinez.demo.features.authentication.exceptions.TokenExceptions
import com.je_martinez.demo.features.authentication.utils.HashEncoder
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.util.*

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService
) {

    fun register(email:String, password:String): RegisterResponse {
        val trimmedEmail = email.trim()
        val user = userRepository.findByEmail(trimmedEmail)
        if (user != null) {
            throw AuthExceptions.userAlreadyExists(trimmedEmail)
        }
        return userRepository.save(
            User(
                email = trimmedEmail,
                hashedPassword = HashEncoder.encode(password)
            )
        ).toResponse()
    }

    fun login(email: String, password: String): Tokens {
        val user = userRepository.findByEmail(email) ?: throw AuthExceptions.invalidCredentials()

        if(!HashEncoder.matches(password, user.hashedPassword)) throw AuthExceptions.invalidCredentials()

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        refreshTokenService.storeRefreshToken(user.id, newRefreshToken)

        return Tokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): Tokens {
        if(!jwtService.validateRefreshToken(refreshToken)) throw TokenExceptions.invalidRefreshToken()

        val userId = jwtService.getUserIdFromToken(refreshToken)

        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            throw TokenExceptions.invalidRefreshToken()
        }

        val hashed = hashToken(refreshToken)

        refreshTokenService.deleteRefreshToken(
            user.id, hashed
        )

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        refreshTokenService.storeRefreshToken(user.id, newRefreshToken)

        return Tokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}