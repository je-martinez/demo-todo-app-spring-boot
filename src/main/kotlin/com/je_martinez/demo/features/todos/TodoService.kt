package com.je_martinez.demo.features.todos

import com.je_martinez.demo.cache.features.todos.TodosCacheSettings
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.exceptions.TodoExceptions
import org.bson.types.ObjectId
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TodoService(
    private val repository: TodoRepository,
) {


    @Cacheable(value = [TodosCacheSettings.FEATURE_NAME], key = TodosCacheSettings.COUNT_KEY)
    fun count(): Long = repository.count()

    @Cacheable(value = [TodosCacheSettings.FEATURE_NAME], key = TodosCacheSettings.FIND_ALL_KEY)
    fun findAll():List<TodoResponse> = repository.findAll().map { it.toResponse() }

    @Cacheable(value = [TodosCacheSettings.FEATURE_NAME], key = TodosCacheSettings.FIND_BY_OWNER_KEY)
    fun findAllByOwner(ownerId: String):List<TodoResponse> =  repository.findTodosByOwnerId(ObjectId(ownerId)).map { it.toResponse() }

    @Cacheable(value = [TodosCacheSettings.FEATURE_NAME], key = TodosCacheSettings.FIND_BY_ID_KEY)
    fun findById(id: String):TodoResponse = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFound(id) }.toResponse()

    fun create(input: TodoRequest, ownerId: String): TodoResponse{
        return repository.save(
            Todo(
                title = input.title,
                description = input.description,
                ownerId = ObjectId(ownerId),
            )
        ).toResponse()
    }

    fun update(id:String, input: TodoRequest):TodoResponse{
        val existingTodo = repository.findById(ObjectId(id)).orElseThrow{
            throw TodoExceptions.notFound(id)
        }
        return repository.save(
            existingTodo.copy(
                title = input.title,
                description = input.description,
            )
        ).toResponse()
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
        return repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        ).toResponse()
    }

    fun markAsUncompleted(id: String): TodoResponse{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }
        return repository.save(
            todo.copy(completed = false, completedAt = null)
        ).toResponse()
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