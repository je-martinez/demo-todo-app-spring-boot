package com.je_martinez.demo.controllers

import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.guards.TodoOwnershipGuard
import com.je_martinez.demo.utils.TodoMockUtils
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.time.Instant

class TodoControllerTests:ApplicationDefinitionTests() {

    @MockkBean(name = "TodoOwnershipGuard")
    lateinit var guard: TodoOwnershipGuard

    @Test
    fun `Get All Todos - Test`(){

        val todos = TodoMockUtils.generateMockDataResponse(5)

        every { todoService.findAll() } returns todos

        val result = todoController.getAll()

        assertInstanceOf<List<TodoResponse>>(result)

        verify(exactly = 1){
            todoService.findAll()
        }

    }

    @Test
    fun `Get All Todos by Owner - Test`(){

        val todos = TodoMockUtils.generateMockDataResponse(5, mockUserId)

        every { todoService.findAllByOwner(mockUserIdAsString) } returns todos

        val result = todoController.getAllByOwner(mockUserIdAsString)

        assertInstanceOf<List<TodoResponse>>(result)

        verify(exactly = 1){
            todoService.findAllByOwner(mockUserIdAsString)
        }

    }

    @Test
    fun `Get Todo By Id - Test`(){

        val todo = TodoMockUtils.generateMockDataResponse(1).first()

        every { guard.isOwner(todo.id, mockUserIdAsString) } returns true

        every { todoService.findById(todo.id) } returns todo

        val result = todoController.getById(todo.id, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1){
            todoService.findById(todo.id)
        }
    }

    @Test
    fun `Create Todo - Test`(){
        val requestBody = TodoRequest(
            title = "Test Title",
            description = "Test Description",
        )

        val mockCreatedTodo = TodoMockUtils.generateMockDataResponse(1).first().copy(
            title = requestBody.title,
            description = requestBody.description,
        )

        every { todoService.create(requestBody, mockUserIdAsString) } returns mockCreatedTodo

        val result = todoController.create(requestBody, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1) {
            todoService.create(requestBody, mockUserIdAsString)
        }

    }

    @Test
    fun `Update Todo - Test`(){

        val requestBody = TodoRequest(
            title = "Test Title Updated",
            description = "Test Description Updated",
        )

        val mockUpdatedTodo = TodoMockUtils.generateMockDataResponse(1).first().copy(
            title = requestBody.title,
            description = requestBody.description,
        )

        every { guard.isOwner(mockUpdatedTodo.id, mockUserIdAsString) } returns true

        every { todoService.update(mockUpdatedTodo.id, requestBody) } returns mockUpdatedTodo

        val result = todoController.update(mockUpdatedTodo.id, requestBody, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1) {
            todoService.update(mockUpdatedTodo.id, requestBody)
        }

    }

    @Test
    fun `Mark as Completed Todo - Test`(){


        val mockTodo = TodoMockUtils.generateMockDataResponse(1).first()
        val mockUpdatedTodo = mockTodo.copy(
            completed = true,
            completedAt = Instant.now()
        )

        every { guard.isOwner(mockUpdatedTodo.id, mockUserIdAsString) } returns true

        every { todoService.markAsCompleted(mockUpdatedTodo.id) } returns mockUpdatedTodo

        val result = todoController.markAsCompleted(mockUpdatedTodo.id, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1) {
            todoService.markAsCompleted(mockUpdatedTodo.id)
        }

    }

    @Test
    fun `Mark as Uncompleted Todo - Test`(){


        val mockTodo = TodoMockUtils.generateMockDataResponse(1).first()
        val mockUpdatedTodo = mockTodo.copy(
            completed = false,
            completedAt = null
        )

        every { guard.isOwner(mockUpdatedTodo.id, mockUserIdAsString) } returns true

        every { todoService.markAsUncompleted(mockUpdatedTodo.id) } returns mockUpdatedTodo

        val result = todoController.markAsUncompleted(mockUpdatedTodo.id, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1) {
            todoService.markAsUncompleted(mockUpdatedTodo.id)
        }

    }

    @Test
    fun `Delete Todo By Id - Test`(){

        val todo = TodoMockUtils.generateMockDataResponse(1).first()

        every { guard.isOwner(todo.id, mockUserIdAsString) } returns true

        every { todoService.delete(todo.id) } just runs

       todoController.delete(todo.id, mockUserIdAsString)

        verify(exactly = 1){
            todoService.delete(todo.id)
        }
    }


}