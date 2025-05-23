package com.je_martinez.demo.guards

import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.exceptions.TodoExceptions
import com.je_martinez.demo.validators.HexString
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Component("TodoOwnershipGuard")
@Validated
class TodoOwnershipGuard(
    private val repository: TodoRepository
){
    fun isOwner(@HexString id: String, userId: String): Boolean{
        val todo = repository.findById(ObjectId(id)).orElseThrow{
            TodoExceptions.notFound(id)
        }
        if(todo.ownerId != ObjectId(userId)){
            throw TodoExceptions.forbidden()
        }
        return true
    }
}