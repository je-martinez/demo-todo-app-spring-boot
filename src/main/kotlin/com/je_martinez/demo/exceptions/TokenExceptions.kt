package com.je_martinez.demo.exceptions

import org.springframework.http.HttpStatusCode
import org.springframework.web.server.ResponseStatusException


class TokenExceptions {
    companion object{
        fun invalidToken(): ResponseStatusException {
            return ResponseStatusException(
                HttpStatusCode.valueOf(401),
                "Invalid token"
            )
        }
    }
}