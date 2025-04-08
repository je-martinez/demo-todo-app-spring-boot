package com.je_martinez.demo.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.je_martinez.demo.controllers.TodoController.TodoResponse
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.exceptions.TodoExceptions
import com.je_martinez.demo.validators.HexString
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Instant

//@Validated
@RestController
@RequestMapping("/api/todos")
class TodoController (private val repository: TodoRepository) {

    data class TodoRequest @JsonCreator constructor(
        @field:NotBlank(message = "Title can't be blank.")
        @field:Min(value = 5, message = "Tile must be at least 5 characters long")
        @JsonProperty("title")
        val title: String,
        @JsonProperty("description")
        val description: String,
    )

    data class TodoResponse(
        val id: String,
        val title: String,
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

    @GetMapping(path = ["/{id}"])
    fun getById(
        @PathVariable @HexString id: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFoundException(id) }
        return todo.toResponse()
    }

    @PostMapping
    fun create(
        @Valid @RequestBody body: TodoRequest
    ):TodoResponse{
        val todo = repository.save(
            Todo(
                title = body.title,
                description = body.description
            )
        )
        return todo.toResponse()
    }

    @PatchMapping(path = ["/mark-as-uncompleted/{id}"])
    fun markAsUncompleted(
        @PathVariable @HexString id: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFoundException(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = false, completedAt = null)
        )

        return updatedTodo.toResponse()
    }

    @PatchMapping(path = ["/mark-as-completed/{id}"])
    fun markAsCompleted(
        @PathVariable @HexString id: String
    ):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFoundException(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        )

        return updatedTodo.toResponse()
    }

    @DeleteMapping(path = ["/{id}"])
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable @HexString id: String
    ) {
        val todo = repository.findById(ObjectId(id)).orElseThrow {
            TodoExceptions.notFoundException(id)
        }
        repository.delete(todo)
    }
}

fun Todo.toResponse(): TodoResponse {
    return TodoResponse(
        id = this.id.toHexString(),
        title = this.title,
        description= this.description,
        createdAt = this.createdAt,
        completedAt = this.completedAt,
        completed = this.completed,
    )
}
