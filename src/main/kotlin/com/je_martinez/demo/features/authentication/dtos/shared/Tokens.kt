package com.je_martinez.demo.features.authentication.dtos.shared

data class Tokens (
    val accessToken: String,
    val refreshToken: String
)