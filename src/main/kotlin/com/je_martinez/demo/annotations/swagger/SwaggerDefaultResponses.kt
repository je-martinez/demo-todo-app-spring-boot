package com.je_martinez.demo.annotations.swagger

import com.je_martinez.demo.dtos.swagger.BadRequestResponse
import com.je_martinez.demo.dtos.swagger.ForbiddenResponse
import com.je_martinez.demo.dtos.swagger.InternalServerResponse
import com.je_martinez.demo.dtos.swagger.NotFoundResponse
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