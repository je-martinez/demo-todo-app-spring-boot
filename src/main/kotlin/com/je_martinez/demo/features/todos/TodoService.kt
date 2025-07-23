package com.je_martinez.demo.features.todos

import com.je_martinez.demo.cache.features.todos.TodoCacheService
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
    private val repository: TodoRepository,
    private val todoCacheService: TodoCacheService
) {

    fun count(): Long {
        val cachedValue = todoCacheService.count()
        if(cachedValue != null){
            return cachedValue
        }
        val count = repository.count()
        todoCacheService.saveCount(count)
        return count
    }

    fun findAll():List<TodoResponse>{
        val cachedTodos = todoCacheService.findAll()
        if(cachedTodos != null){
            return cachedTodos
        }
        val todos = repository.findAll().map { it.toResponse() }
        todoCacheService.saveFindAll(todos)
        return todos
    }

    fun findAllByOwner(ownerId: String):List<TodoResponse>{
        val cachedTodos = todoCacheService.findByOwner(ownerId)
        if(cachedTodos != null){
            return cachedTodos
        }
        val todos = repository.findTodosByOwnerId(ObjectId(ownerId)).map { it.toResponse() }
        todoCacheService.saveFindByOwner(ownerId, todos)
        return todos
    }

    fun findById(id: String):TodoResponse{
        val cachedTodo = todoCacheService.findById(id)
        if(cachedTodo != null){
            return cachedTodo
        }
        val todo = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFound(id) }.toResponse()
        todoCacheService.saveFindById(todo)
        return todo
    }

    fun create(input: TodoRequest, ownerId: String): TodoResponse{
        val todo = repository.save(
            Todo(
                title = input.title,
                description = input.description,
                ownerId = ObjectId(ownerId),
            )
        ).toResponse()
        todoCacheService.invalidate(
            type = TodoCacheService.InvalidationType.CREATE,
            args = TodoCacheService.InvalidationArgs(ownerId = ownerId)
        )
        return todo
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
        ).toResponse()
        todoCacheService.invalidate(
            type = TodoCacheService.InvalidationType.UPDATE,
            args = TodoCacheService.InvalidationArgs(
                ownerId = todo.ownerId,
                todoId = todo.id
            )
        )
        return todo
    }

    fun delete(id: String){
        val todo = repository.findById(ObjectId(id)).orElseThrow {
            TodoExceptions.notFound(id)
        }
        repository.delete(todo)
        todoCacheService.invalidate(
            type = TodoCacheService.InvalidationType.DELETE,
            args = TodoCacheService.InvalidationArgs(
                ownerId = todo.ownerId.toHexString(),
                todoId = todo.id.toHexString()
            )
        )
    }

    fun markAsCompleted(id:String):TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }
        val updatedTodo = repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        ).toResponse()
        todoCacheService.invalidate(
            type = TodoCacheService.InvalidationType.UPDATE,
            args = TodoCacheService.InvalidationArgs(
                ownerId = todo.ownerId.toHexString(),
                todoId = todo.id.toHexString(),
                invalidateCount = false
            )
        )
        return updatedTodo
    }

    fun markAsUncompleted(id: String): TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }
        val updatedTodo = repository.save(
            todo.copy(completed = false, completedAt = null)
        ).toResponse()
        todoCacheService.invalidate(
            type = TodoCacheService.InvalidationType.UPDATE,
            args = TodoCacheService.InvalidationArgs(
                ownerId = todo.ownerId.toHexString(),
                todoId = todo.id.toHexString(),
                invalidateCount = false
            )
        )
        return updatedTodo
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