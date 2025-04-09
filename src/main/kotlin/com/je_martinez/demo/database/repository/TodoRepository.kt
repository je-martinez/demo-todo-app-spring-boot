package com.je_martinez.demo.database.repository

import com.je_martinez.demo.database.models.Todo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface TodoRepository: MongoRepository<Todo, ObjectId> {
    fun findTodosByOwnerId(ownerId: ObjectId): MutableList<Todo>
}