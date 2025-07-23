package com.je_martinez.demo.cache.features.todos

import com.fasterxml.jackson.core.type.TypeReference
import com.je_martinez.demo.cache.CacheService
import com.je_martinez.demo.dtos.todos.TodoResponse
import org.springframework.stereotype.Service
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

@Service
class TodoCacheService(
    private val cacheService: CacheService
) {

    enum class InvalidationType(val value:String){
        CREATE("CREATE"),
        UPDATE("UPDATE"),
        DELETE("DELETE")
    }

    data class InvalidationArgs(
        val ownerId: String? = null,
        val todoId:String? = null,
        val invalidateCount: Boolean = true
    )

    private enum class Keys(val value:String, val timeout: java.time.Duration){
        COUNT("Todos:Count", 30.toDuration(DurationUnit.SECONDS).toJavaDuration()),
        FIND_ALL("Todos:*", 30.toDuration(DurationUnit.SECONDS).toJavaDuration()),
        FIND_BY_OWNER("Todos:Owner:", 30.toDuration(DurationUnit.SECONDS).toJavaDuration()),
        FIND_BY_ID("Todos:", 1.toDuration(DurationUnit.MINUTES).toJavaDuration())
    }

    fun invalidate(type: InvalidationType, args: InvalidationArgs = InvalidationArgs()){
        when(type){
            InvalidationType.CREATE -> {
                args.invalidateCount && invalidateCount()
                invalidateFindAll()
                args.ownerId != null && invalidateFindByOwner(args.ownerId)
            }
            InvalidationType.UPDATE, InvalidationType.DELETE  -> {
                args.invalidateCount && invalidateCount()
                invalidateFindAll()
                args.ownerId != null && invalidateFindByOwner(args.ownerId)
                args.todoId != null && invalidateFindBy(args.todoId)
            }
        }
    }

    fun saveCount(count: Long) = cacheService.save(Keys.COUNT.value, count, Keys.COUNT.timeout)
    fun count(): Long? = cacheService.find(Keys.COUNT.value)?.toLong()
    fun invalidateCount() = cacheService.delete(Keys.COUNT.value)

    fun saveFindAll(todos: List<TodoResponse>) = cacheService.save(Keys.FIND_ALL.value, todos, Keys.FIND_ALL.timeout)
    fun findAll(): List<TodoResponse>? = cacheService.findAsType(Keys.FIND_ALL.value, object : TypeReference<List<TodoResponse>>() {})
    fun invalidateFindAll() = cacheService.delete(Keys.FIND_ALL.value)

    fun getFindByOwnerMarker(ownerId: String) = "${Keys.FIND_BY_OWNER.value}${ownerId}"
    fun saveFindByOwner(ownerId:String, todos: List<TodoResponse>) = cacheService.save(getFindByOwnerMarker(ownerId), todos, Keys.FIND_BY_OWNER.timeout)
    fun findByOwner(ownerId:String): List<TodoResponse>? = cacheService.findAsType(getFindByOwnerMarker(ownerId), object : TypeReference<List<TodoResponse>>() {})
    fun invalidateFindByOwner(ownerId:String) = cacheService.delete(getFindByOwnerMarker(ownerId))

    fun getFindByIdMarket(id: String) = "${Keys.FIND_BY_ID.value}${id}"
    fun saveFindById(todo: TodoResponse) = cacheService.save(getFindByIdMarket(todo.id), todo, Keys.FIND_BY_ID.timeout)
    fun findById(id: String): TodoResponse? = cacheService.findAsType(getFindByIdMarket(id), object : TypeReference<TodoResponse>() {})
    fun invalidateFindBy(id:String) = cacheService.delete(getFindByIdMarket(id))

}