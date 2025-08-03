package com.je_martinez.demo.features.authentication.commands.register

import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.dtos.responses.RegisterResponse
import com.je_martinez.demo.features.authentication.dtos.responses.toResponse
import com.je_martinez.demo.features.authentication.exceptions.AuthExceptions
import com.je_martinez.demo.features.authentication.utils.HashEncoder
import com.trendyol.kediatr.RequestHandler
import org.springframework.stereotype.Component

@Component
class RegisterCommandHandler(
    private val userRepository: UserRepository,
    ): RequestHandler<RegisterCommand, RegisterResponse>{

    override suspend fun handle(request: RegisterCommand): RegisterResponse{
        val trimmedEmail = request.email.trim()
        val user = userRepository.findByEmail(trimmedEmail)
        if (user != null) {
            throw AuthExceptions.userAlreadyExists(trimmedEmail)
        }
        return userRepository.save(
            User(
                email = trimmedEmail,
                hashedPassword = HashEncoder.encode(request.password)
            )
        ).toResponse()
    }
}