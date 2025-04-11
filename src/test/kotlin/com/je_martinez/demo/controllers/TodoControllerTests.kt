package com.je_martinez.demo.controllers

import com.je_martinez.demo.dtos.todos.TodoResponse
import com.je_martinez.demo.features.authentication.JwtService
import com.je_martinez.demo.guards.TodoOwnershipEvaluator
import com.je_martinez.demo.utils.TodoMockUtils
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf

class TodoControllerTests:ApplicationDefinitionTests() {

    @MockkBean
    lateinit var evaluator: TodoOwnershipEvaluator

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

        every { todoService.findById(todo.id) } returns todo

        val result = todoController.getById(todo.id, mockUserIdAsString)

        assertInstanceOf<TodoResponse>(result)

        verify(exactly = 1){
            todoService.findById(todo.id)
        }
    }

}