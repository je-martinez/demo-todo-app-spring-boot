package com.je_martinez.demo.features.todos.swagger

import com.je_martinez.demo.features.todos.dtos.TodoResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse


@ApiResponse(
    responseCode = "200",
    description = "OK",
    content = [Content(schema = Schema(implementation = TodoResponse::class))]
)
annotation class SwaggerTodoResponse