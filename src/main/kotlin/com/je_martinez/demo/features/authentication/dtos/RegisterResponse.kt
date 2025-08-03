package com.je_martinez.demo.features.authentication.dtos

import com.je_martinez.demo.database.models.User

data class RegisterResponse(
    val id: String,
    val email: String
)

fun User.toResponse() : RegisterResponse{
    return RegisterResponse(
        id = this.id.toHexString(),
        email = this.email
    )
}
