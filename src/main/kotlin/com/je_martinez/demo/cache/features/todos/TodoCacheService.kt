package com.je_martinez.demo.cache.features.todos

import com.fasterxml.jackson.core.type.TypeReference
import com.je_martinez.demo.cache.CacheService
import com.je_martinez.demo.dtos.todos.TodoResponse
import org.springframework.stereotype.Service

@Service
class TodoCacheService(
    private val cacheService: CacheService
) {

    private enum class Keys(val value:String){
        COUNT("Todos:Count"),
        FIND_ALL("Todos:*"),
        FIND_BY_OWNER("Todos:Owner:"),
        FIND_BY_ID("Todos:")
    }

    fun saveCount(count: Int) = cacheService.save(Keys.COUNT.value, count)
    fun count(count: Int): Int? = cacheService.find(Keys.COUNT.value)?.toInt()
    fun invalidateCount() = cacheService.delete(Keys.COUNT.value)

    fun saveFindAll(todos: List<TodoResponse>) = cacheService.save(Keys.FIND_ALL.value, todos)
    fun findAll(todos: List<TodoResponse>): List<TodoResponse>? = cacheService.findAsType(Keys.FIND_ALL.value, object : TypeReference<List<TodoResponse>>() {})
    fun invalidateFindAll() = cacheService.delete(Keys.FIND_ALL.value)

    fun getFindByOwnerMarker(ownerId: String) = "${Keys.FIND_BY_OWNER.value}${ownerId}"
    fun saveFindByOwner(ownerId:String, todos: List<TodoResponse>) = cacheService.save(getFindByOwnerMarker(ownerId), todos)
    fun findByOwner(ownerId:String, todos: List<TodoResponse>): List<TodoResponse>? = cacheService.findAsType(getFindByOwnerMarker(ownerId), object : TypeReference<List<TodoResponse>>() {})
    fun invalidateFindByOwner(ownerId:String) = cacheService.delete(getFindByOwnerMarker(ownerId))

    fun getFindByIdMarket(id: String) = "${Keys.FIND_BY_ID.value}${id}"
    fun saveFindById(todo: TodoResponse) = cacheService.save(getFindByIdMarket(todo.id), todo)
    fun findById(todo: TodoResponse): TodoResponse? = cacheService.findAsType(getFindByIdMarket(todo.id), object : TypeReference<TodoResponse>() {})
    fun invalidateFindBy(id:String) = cacheService.delete(getFindByIdMarket(id))

}