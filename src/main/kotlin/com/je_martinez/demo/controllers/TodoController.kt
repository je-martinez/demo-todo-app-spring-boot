package com.je_martinez.demo.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.je_martinez.demo.controllers.TodoController.TodoResponse
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.exceptions.TodoExceptions
import com.je_martinez.demo.security.current_user.CurrentUserId
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
class TodoController (private val repository: TodoRepository) {

    data class TodoRequest @JsonCreator constructor(
        @field:NotBlank(message = "Title can't be blank.")
        //@field:Min(value = 5, message = "Tile must be at least 5 characters long")
        @JsonProperty("title")
        val title: String,
        @JsonProperty("description")
        val description: String,
    )

    data class TodoResponse(
        val id: String,
        val title: String,
        val ownerId: String,
        val description: String,
        val createdAt: Instant,
        val completedAt: Instant?,
        val completed: Boolean
    )

    @GetMapping
    fun getAll(
    ):List<TodoResponse>{
        val todos = repository.findAll()
        return todos.map {
            it.toResponse()
        }
    }

    @GetMapping
    @RequestMapping("/by-owner", method = [RequestMethod.GET])
    fun getAllByOwner(@CurrentUserId userId: String):List<TodoResponse>{
        val todos = repository.findTodosByOwnerId(ObjectId(userId))
        return todos.map {
            it.toResponse()
        }
    }

    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @GetMapping(path = ["/{id}"])
    fun getById(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFound(id) }
        return todo.toResponse()
    }

    @PostMapping
    fun create(
        @Valid @RequestBody body: TodoRequest,
        @CurrentUserId userId: String
    ):TodoResponse{
        val todo = repository.save(
            Todo(
                title = body.title,
                description = body.description,
                ownerId = ObjectId(userId),
            )
        )
        return todo.toResponse()
    }

    @PatchMapping(path = ["/mark-as-uncompleted/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    fun markAsUncompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = false, completedAt = null)
        )

        return updatedTodo.toResponse()
    }

    @PatchMapping(path = ["/mark-as-completed/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    fun markAsCompleted(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        )

        return updatedTodo.toResponse()
    }

    @DeleteMapping(path = ["/{id}"])
    @PreAuthorize("@TodoOwnershipGuard.isOwner(#id, #userId)")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable @HexString id: String,
        @CurrentUserId userId: String
    ) {
        val todo = repository.findById(ObjectId(id)).orElseThrow {
            TodoExceptions.notFound(id)
        }
        repository.delete(todo)
    }
}

fun Todo.toResponse(): TodoResponse {
    return TodoResponse(
        id = this.id.toHexString(),
        title = this.title,
        description = this.description,
        ownerId = this.ownerId.toHexString(),
        createdAt = this.createdAt,
        completedAt = this.completedAt,
        completed = this.completed,
    )
}
