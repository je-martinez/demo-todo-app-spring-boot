package com.je_martinez.demo.features.todos.services

import com.je_martinez.demo.features.todos.cache.TodosCacheSettings
import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.features.todos.exceptions.TodoExceptions
import com.je_martinez.demo.features.todos.dtos.TodoRequest
import com.je_martinez.demo.features.todos.dtos.TodoResponse
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TodoService(
    private val repository: TodoRepository,
) {

    @Cacheable(value = [TodosCacheSettings.COUNT_KEY])
    fun count(): Long = repository.count()

    @Cacheable(value = [TodosCacheSettings.FIND_ALL_KEY])
    fun findAll():List<TodoResponse> = repository.findAll().map { it.toResponse() }

    @Cacheable(value = [TodosCacheSettings.FIND_BY_OWNER_KEY], key = "#ownerId")
    fun findAllByOwner(ownerId: String):List<TodoResponse> =  repository.findTodosByOwnerId(ObjectId(ownerId)).map { it.toResponse() }

    @Cacheable(value = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#id")
    fun findById(id: String): TodoResponse = repository.findById(ObjectId(id)).orElseThrow{ TodoExceptions.notFound(id) }.toResponse()

    @Caching(
        evict = [
            CacheEvict(cacheNames = [TodosCacheSettings.COUNT_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_ALL_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#ownerId")
        ],
        put = [
            CachePut(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#result.id")
        ]
    )
    fun create(input: TodoRequest, ownerId: String): TodoResponse {
        return repository.save(
            Todo(
                title = input.title,
                description = input.description,
                ownerId = ObjectId(ownerId),
            )
        ).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_ALL_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_OWNER_KEY], key = "#ownerId")
        ],
        put = [
            CachePut(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#id")
        ]
    )
    fun update(id: String, input: TodoRequest, ownerId: String): TodoResponse {
        val existingTodo = repository.findByIdAndOwnerId(ObjectId(id), ObjectId(ownerId)).orElseThrow{
            throw TodoExceptions.notFound(id)
        }
        return repository.save(
            existingTodo.copy(
                title = input.title,
                description = input.description,
            )
        ).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = [TodosCacheSettings.COUNT_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_ALL_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_OWNER_KEY], key = "#ownerId"),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#id")
        ]
    )
    fun delete(id: String, ownerId: String){
        val existingTodo = repository.findByIdAndOwnerId(ObjectId(id), ObjectId(ownerId)).orElseThrow{
            throw TodoExceptions.notFound(id)
        }
        repository.delete(existingTodo)
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_ALL_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_OWNER_KEY], key = "#ownerId")
        ],
        put = [
            CachePut(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#id")
        ]
    )
    fun markAsCompleted(id: String, ownerId: String): TodoResponse {
        val todo = repository.findByIdAndOwnerId(ObjectId(id), ObjectId(ownerId)).orElseThrow{
            throw TodoExceptions.notFound(id)
        }
        return repository.save(
            todo.copy(completed = true, completedAt = Instant.now())
        ).toResponse()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_ALL_KEY]),
            CacheEvict(cacheNames = [TodosCacheSettings.FIND_BY_OWNER_KEY], key = "#ownerId")
        ],
        put = [
            CachePut(cacheNames = [TodosCacheSettings.FIND_BY_ID_KEY], key = "#id")
        ]
    )
    fun markAsUncompleted(id: String, ownerId: String): TodoResponse {
        val todo = repository.findByIdAndOwnerId(ObjectId(id), ObjectId(ownerId)).orElseThrow{
            throw TodoExceptions.notFound(id)
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