package com.je_martinez.demo.guards

import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.exceptions.TodoExceptions
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component("TodoOwnershipGuard")
class TodoOwnershipEvaluator(
    private val repository: TodoRepository
){
    fun isOwner(id: String, userId: ObjectId): Boolean{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }
        if(todo.ownerId != userId){
            throw TodoExceptions.forbidden()
        }
        return true
    }
}