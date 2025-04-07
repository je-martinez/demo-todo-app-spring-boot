package com.je_martinez.demo.controllers

import com.je_martinez.demo.controllers.TodoController.TodoResponse
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/todos")
class TodoController (private val repository: TodoRepository) {

    data class TodoRequest(
        val title: String,
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

    @PostMapping
    fun create(
        @RequestBody body: TodoRequest
    ):TodoResponse{
        val todo = repository.save(
            Todo(
                title = body.title,
                description = body.description
            )
        )
        return todo.toResponse();
    }
}

fun Todo.toResponse(): TodoController.TodoResponse {
    return TodoResponse(
        id = this.id.toHexString(),
        title = this.title,
        description= this.description,
        createdAt = this.createdAt,
        completedAt = this.completedAt,
        completed = this.completed,
    );
}
