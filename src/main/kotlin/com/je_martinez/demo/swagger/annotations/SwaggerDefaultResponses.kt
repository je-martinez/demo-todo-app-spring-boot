package com.je_martinez.demo.swagger.annotations

import com.je_martinez.demo.swagger.dtos.BadRequestResponse
import com.je_martinez.demo.swagger.dtos.ForbiddenResponse
import com.je_martinez.demo.swagger.dtos.InternalServerResponse
import com.je_martinez.demo.swagger.dtos.NotFoundResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(schema = Schema(implementation = BadRequestResponse::class))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = [Content(schema = Schema(implementation = ForbiddenResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = [Content(schema = Schema(implementation = NotFoundResponse::class))]
        ),
        ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = [Content(schema = Schema(implementation = InternalServerResponse::class))]
        )
    ]
)annotation class SwaggerDefaultResponses {}