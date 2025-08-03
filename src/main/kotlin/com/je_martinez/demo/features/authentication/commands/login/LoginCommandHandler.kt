package com.je_martinez.demo.features.authentication.commands.login

import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.je_martinez.demo.features.authentication.exceptions.AuthExceptions
import com.je_martinez.demo.features.authentication.services.JwtService
import com.je_martinez.demo.features.authentication.services.RefreshTokenService
import com.je_martinez.demo.features.authentication.utils.HashEncoder
import com.trendyol.kediatr.RequestHandler
import org.springframework.stereotype.Component

@Component
class LoginCommandHandler(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    ) : RequestHandler<LoginCommand, Tokens> {
    override suspend fun handle(request: LoginCommand): Tokens {
        val user = userRepository.findByEmail(request.email) ?: throw AuthExceptions.invalidCredentials()

        if(!HashEncoder.matches(request.password, user.hashedPassword)) throw AuthExceptions.invalidCredentials()

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        refreshTokenService.storeRefreshToken(user.id, newRefreshToken)

        return Tokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}