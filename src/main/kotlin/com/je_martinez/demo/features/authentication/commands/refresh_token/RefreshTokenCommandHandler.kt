package com.je_martinez.demo.features.authentication.commands.refresh_token

import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.je_martinez.demo.features.authentication.exceptions.TokenExceptions
import com.je_martinez.demo.features.authentication.services.JwtService
import com.je_martinez.demo.features.authentication.services.RefreshTokenService
import com.je_martinez.demo.features.authentication.utils.TokenUtils
import com.trendyol.kediatr.RequestHandler
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class RefreshTokenCommandHandler(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
) : RequestHandler<RefreshTokenCommand, Tokens>{
    override suspend fun handle(request: RefreshTokenCommand): Tokens {
        if(!jwtService.validateRefreshToken(request.refreshToken)) throw TokenExceptions.invalidRefreshToken()

        val userId = jwtService.getUserIdFromToken(request.refreshToken)

        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            throw TokenExceptions.invalidRefreshToken()
        }

        val hashed = TokenUtils.hashToken(request.refreshToken)

        refreshTokenService.deleteRefreshToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        refreshTokenService.storeRefreshToken(user.id, newRefreshToken)

        return Tokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

}