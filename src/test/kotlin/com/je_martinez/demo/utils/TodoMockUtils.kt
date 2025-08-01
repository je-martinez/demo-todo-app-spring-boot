package com.je_martinez.demo.utils

import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.features.todos.dtos.TodoResponse
import org.bson.types.ObjectId
import java.time.Instant

object TodoMockUtils {
    fun generateMockDataResponse(size:Int = 10, ownerId:ObjectId = ObjectId()):List<TodoResponse>{
        val mockData = mutableListOf<TodoResponse>()
        for(i in 1..size) {
            mockData.add(
                TodoResponse(
                    id = ObjectId().toHexString(),
                    title = "Title $i",
                    description = "Description $i",
                    ownerId = ownerId.toHexString(),
                    createdAt = Instant.now(),
                    completedAt = if(i % 2 == 0) Instant.now() else null,
                    completed = i % 2 == 0
                )
            )
        }
        return mockData
    }

    fun generateMockData(size:Int = 10, ownerId:ObjectId = ObjectId()):List<Todo>{
        val mockData = mutableListOf<Todo>()
        for(i in 1..size) {
            mockData.add(
                Todo(
                    title = "Title $i",
                    description = "Description $i",
                    ownerId = ownerId,
                    createdAt = Instant.now(),
                    completedAt = if(i % 2 == 0) Instant.now() else null,
                    completed = i % 2 == 0
                )
            )
        }
        return mockData
    }
}