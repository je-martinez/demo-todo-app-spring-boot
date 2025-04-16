package com.je_martinez.demo.dtos.swagger

data class BadRequestResponse(
    val message: String,
    val errors: List<String>
)