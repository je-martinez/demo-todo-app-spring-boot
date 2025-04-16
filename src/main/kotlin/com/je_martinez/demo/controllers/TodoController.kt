package com.je_martinez.demo.controllers

import com.je_martinez.demo.annotations.current_user.CurrentUserId
import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.features.todos.TodoService
import com.je_martinez.demo.validators.HexString
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/todos")
@EnableMethodSecurity
@Tag(name = "Todo Controller", description = "Controller for Todo Entity Operations")
class TodoController (private val service: TodoService) {

    @GetMapping
    @Operation(summary = "Get all todos endpoint")
    fun getAll():List<TodoResponse> = service.findAll()

    @GetMapping("/by-owner")
    @Operation(summary = "Get all todos by owner endpoint")
    fun getAllByOwner(@CurrentUserId userId: String):List<TodoResponse> = service.findAllByOwner(userId)

    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @GetMapping(path = ["/{id}"])
    @Operation(summary = "Get todo by id")
    fun getById(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.findById(id)

    @PostMapping
    @Operation(summary = "Create a new todo endpoint")
    fun create(
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ):TodoResponse = service.create(body, userId)

    @PutMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Update an existing todo endpoint")
    fun update(
        @PathVariable @HexString id: String,
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ):TodoResponse = service.update(id, body)

    @PatchMapping(path = ["/mark-as-uncompleted/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Mark existing todo as uncompleted endpoint")
    fun markAsUncompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.markAsUncompleted(id)

    @PatchMapping(path = ["/mark-as-completed/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @Operation(summary = "Mark existing todo as completed endpoint")
    fun markAsCompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.markAsCompleted(id)

    @DeleteMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete todo as completed endpoint")
    fun delete(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    )  = service.delete(id)
}


