package com.je_martinez.demo.features.authentication.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object AuthExceptions {
    fun userAlreadyExists(email: String): ResponseStatusException {
        return ResponseStatusException(
            HttpStatus.CONFLICT,
            "User with email $email already exists"
        )
    }
    fun invalidCredentials(): ResponseStatusException {
        return ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password"
        )
    }
}