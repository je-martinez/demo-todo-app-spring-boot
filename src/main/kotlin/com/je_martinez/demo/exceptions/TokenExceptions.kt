package com.je_martinez.demo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException


class TokenExceptions {
    companion object{
        fun invalidToken(): ResponseStatusException {
            return ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid token"
            )
        }

        fun invalidRefreshToken(): ResponseStatusException {
            return ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid refresh token"
            )
        }

        fun invalidRefreshTokenUsedOrExpired(): ResponseStatusException {
            return ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid refresh token used (or expired)"
            )
        }
    }
}