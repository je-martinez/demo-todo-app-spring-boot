package com.je_martinez.demo.features.todos.controllers

import com.je_martinez.demo.features.authentication.annotations.current_user.CurrentUserId
import com.je_martinez.demo.swagger.annotations.SwaggerDefaultResponses
import com.je_martinez.demo.features.todos.swagger.SwaggerTodoResponse
import com.je_martinez.demo.features.todos.swagger.SwaggerTodosResponse
import com.je_martinez.demo.features.todos.dtos.requests.TodoRequest
import com.je_martinez.demo.features.todos.dtos.responses.TodoResponse
import com.je_martinez.demo.features.todos.services.TodoService
import com.je_martinez.demo.features.authentication.validators.HexString
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/todos")
@EnableMethodSecurity
@Tag(name = "Todo Controller", description = "Controller for Todo Entity Operations")
@SecurityRequirement(name = "bearerAuth")
class TodoController (private val service: TodoService) {

    @GetMapping
    @Operation(summary = "Get all todos endpoint")
    @SwaggerTodosResponse
    @SwaggerDefaultResponses
    fun getAll():List<TodoResponse> = service.findAll()

    @GetMapping("/by-owner")
    @Operation(summary = "Get all todos by owner endpoint")
    @SwaggerTodosResponse
    @SwaggerDefaultResponses
    fun getAllByOwner(@CurrentUserId userId: String):List<TodoResponse> = service.findAllByOwner(userId)

    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @GetMapping(path = ["/{id}"])
    @Operation(summary = "Get todo by id")
    @SwaggerTodoResponse
    @SwaggerDefaultResponses
    fun getById(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ): TodoResponse = service.findById(id)

    @PostMapping
    @Operation(summary = "Create a new todo endpoint")
    @SwaggerTodoResponse
    @SwaggerDefaultResponses
    fun create(
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ): TodoResponse = service.create(body, userId)

    @PutMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Update an existing todo endpoint")
    @SwaggerTodoResponse
    @SwaggerDefaultResponses
    fun update(
        @PathVariable @HexString id: String,
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ): TodoResponse = service.update(id, body, userId)

    @PatchMapping(path = ["/mark-as-uncompleted/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Mark existing todo as uncompleted endpoint")
    @SwaggerTodoResponse
    @SwaggerDefaultResponses
    fun markAsUncompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ): TodoResponse = service.markAsUncompleted(id, userId)

    @PatchMapping(path = ["/mark-as-completed/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Mark existing todo as completed endpoint")
    @SwaggerTodoResponse
    @SwaggerDefaultResponses
    fun markAsCompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ): TodoResponse = service.markAsCompleted(id,userId)

    @DeleteMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete todo as completed endpoint")
    @SwaggerDefaultResponses
    fun delete(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    )  = service.delete(id, userId)
}