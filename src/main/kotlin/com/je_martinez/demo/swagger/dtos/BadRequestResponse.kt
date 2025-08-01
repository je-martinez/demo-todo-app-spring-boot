package com.je_martinez.demo.swagger.dtos

data class BadRequestResponse(
    val message: String,
    val errors: List<String>
)