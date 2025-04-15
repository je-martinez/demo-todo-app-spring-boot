package com.je_martinez.demo.features.todos

import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.exceptions.TodoExceptions
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TodoService(
    private val repository: TodoRepository
) {

    fun count(): Long {
        return repository.count()
    }

    fun findAll():List<TodoResponse>{
        val todos = repository.findAll()
        return todos.map {
            it.toResponse()
        }
    }

    fun findAllByOwner(ownerId: String):List<TodoResponse>{
        val todos = repository.findTodosByOwnerId(ObjectId(ownerId))
        return todos.map {
            it.toResponse()
        }
    }

    fun findById(id: String):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFound(id) }
        return todo.toResponse()
    }

    fun create(input: TodoRequest, ownerId: String): TodoResponse{
        val todo = repository.save(
            Todo(
                title = input.title,
                description = input.description,
                ownerId = ObjectId(ownerId),
            )
        )
        return todo.toResponse()
    }

    fun update(id:String, input: TodoRequest):TodoResponse{
        val existingTodo = repository.findById(ObjectId(id)).orElseThrow{
            throw TodoExceptions.notFound(id)
        }

        val todo = repository.save(
            existingTodo.copy(
                title = input.title,
                description = input.description,
            )
        )
        return todo.toResponse()
    }

    fun delete(id: String){
        val todo = repository.findById(ObjectId(id)).orElseThrow {
            TodoExceptions.notFound(id)
        }
        repository.delete(todo)
    }

    fun markAsCompleted(id:String):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        )

        return updatedTodo.toResponse()
    }

    fun markAsUncompleted(id: String): TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }

        val updatedTodo = repository.save(
            todo.copy(completed = false, completedAt = null)
        )

        return updatedTodo.toResponse()
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
}