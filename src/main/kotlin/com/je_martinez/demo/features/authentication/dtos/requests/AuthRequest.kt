package com.je_martinez.demo.features.authentication.dtos.requests

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class AuthRequest(
    @field:Email(message = "Invalid email format.")
    @field:Schema(name = "email", example = "joe.doe@mail.com", type = "string", required = true)
    val email: String,
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
        message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
    )
    @field:Schema(name = "password", example = "TestPassword1234!", type = "string")
    val password: String
)