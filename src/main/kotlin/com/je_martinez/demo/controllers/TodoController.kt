package com.je_martinez.demo.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.exceptions.TodoExceptions
import com.je_martinez.demo.annotations.current_user.CurrentUserId
import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.features.todos.TodoService
import com.je_martinez.demo.validators.HexString
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Instant

@Validated
@RestController
@RequestMapping("/api/todos")
@EnableMethodSecurity
class TodoController (private val service: TodoService) {

    @GetMapping
    fun getAll():List<TodoResponse> = service.findAll()

    @GetMapping
    @RequestMapping("/by-owner")
    fun getAllByOwner(@CurrentUserId userId: String):List<TodoResponse> = service.findAllByOwner(userId)

    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @GetMapping(path = ["/{id}"])
    fun getById(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.findById(id)

    @PostMapping
    fun create(
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ):TodoResponse = service.create(body, userId)

    @PutMapping
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @RequestMapping(path = ["/{id}"])
    fun update(
        @PathVariable @HexString id: String,
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ):TodoResponse = service.update(id, body)

    @PatchMapping(path = ["/mark-as-uncompleted/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    fun markAsUncompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.markAsUncompleted(id)

    @PatchMapping(path = ["/mark-as-completed/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    fun markAsCompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse = service.markAsCompleted(id)

    @DeleteMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    )  = service.delete(id)
}


