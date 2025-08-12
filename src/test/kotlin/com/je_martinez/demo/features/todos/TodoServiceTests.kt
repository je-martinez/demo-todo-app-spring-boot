package com.je_martinez.demo.features.todos

import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.features.todos.dtos.TodoRequest
import com.je_martinez.demo.features.todos.dtos.responses.TodoResponse
import com.je_martinez.demo.features.todos.exceptions.TodoExceptions
import com.je_martinez.demo.features.todos.services.TodoService
import com.je_martinez.demo.utils.TodoMockUtils
import com.ninjasquad.springmockk.SpykBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import kotlin.test.*

@SpringBootTest
class TodoServiceTests {

    @SpykBean
    private lateinit var repository: TodoRepository

    @SpykBean
    private lateinit var service: TodoService

    private val mockUser = ObjectId()
    private val mockUserAsString = mockUser.toHexString()
    private val existingTodos = mutableListOf<Todo>()

    @BeforeTest
    fun setup(){
        val todos = TodoMockUtils.generateMockData(5)
        repository.saveAll(todos)
        val todosWithCustomOwner = TodoMockUtils.generateMockData(5, mockUser)
        repository.saveAll(todosWithCustomOwner)
        existingTodos.addAll(todos + todosWithCustomOwner)
    }

    @AfterTest()
    fun teardown(){
        repository.deleteAll()
    }

    @BeforeEach
    fun reset(){
        clearAllMocks()
    }

    @Test
    fun `Should return a list of todos`(){
        val todos = service.findAll()
        assertEquals(todos.isEmpty(), false)
        assertInstanceOf<List<TodoResponse>>(todos)
        assertEquals(todos.size, service.count().toInt())
        verify(exactly = 1) { repository.findAll()  }
    }

    @Test
    fun `Should return a list of todos with the same owner`(){
        val todos = service.findAllByOwner(ownerId = mockUserAsString)
        assertEquals(todos.isEmpty(), false)
        assertInstanceOf<List<TodoResponse>>(todos)
        todos.forEach{
            assertInstanceOf<TodoResponse>(it)
            assertEquals(mockUserAsString, it.ownerId)
        }
        verify(exactly = 1) { service.findAllByOwner(ownerId = mockUserAsString)  }
    }

    @Test
    fun `Should return an entity by id`(){
        val id = existingTodos.first().id.toHexString()
        val todo = service.findById(id)
        assertInstanceOf<TodoResponse>(todo)
        assertEquals(id, todo.id)
        verify(exactly = 1) {  service.findById(id)  }
    }

    @Test
    fun `Should throw an error if todo with id doesn't exists`(){
        val id = ObjectId().toString()
        try{
            service.findById(id)
            throw Exception("Method above should throw an error")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every { service.findById(id) } throws TodoExceptions.notFound(id)
            verify(exactly = 1) { service.findById(id) }
        }
    }

    @Test
    fun `Should be able to create a todo`(){
        val title = "New Todo"
        val description = "New Description"
        val input = TodoRequest(
            title = title,
            description = description
        )
        val newTodo = service.create(
            input,
            mockUserAsString
        )
        assertInstanceOf<TodoResponse>(newTodo)
        assertEquals(title, newTodo.title)
        assertEquals(description, newTodo.description)
        verify(exactly = 1) { repository.save(any<Todo>())  }
    }

    @Test
    fun `Should be able to update a todo`(){
        val id = existingTodos.first().id.toHexString()
        val title = "New Todo"
        val description = "New Description"
        val input = TodoRequest(
            title = title,
            description = description
        )
        val updatedTodo = service.update(id, input)

        assertInstanceOf<TodoResponse>(updatedTodo)
        assertEquals(id, updatedTodo.id)
        assertEquals(title, updatedTodo.title)
        assertEquals(description, updatedTodo.description)
        verify(exactly = 1) { repository.findById(ObjectId(id)) }
        verify(exactly = 1) { repository.save(any<Todo>()) }
    }

    @Test
    fun `Should throw an error on update a todo if doesn't exists`(){
        val id = ObjectId().toHexString()
        val title = "New Todo"
        val description = "New Description"
        val input = TodoRequest(
            title = title,
            description = description
        )
        try{
            service.update(id, input)
            throw Exception("Method above should throw an error")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {  service.update(id, input) } throws TodoExceptions.notFound(id)
            verify(exactly = 1) { repository.findById(ObjectId(id)) }
        }
    }

    @Test
    fun `Should be able to delete a todo`(){
        val title = "New Todo"
        val description = "New Description"
        val input = TodoRequest(
            title = title,
            description = description
        )
        val newTodo = service.create(
            input,
            mockUserAsString
        )

        assertInstanceOf<TodoResponse>(newTodo)

        clearAllMocks()

        service.delete(newTodo.id)
        verify(exactly = 1) { repository.delete(any<Todo>()) }
    }

    @Test
    fun `Should throw an error on delete todo if todo doesn't exists`(){
        val id = ObjectId().toHexString()

        try{
            service.delete(id)
            throw Exception("Method above should throw an error")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            verify(exactly = 1) { repository.findById(ObjectId(id)) }
            every { service.delete(id) } throws TodoExceptions.notFound(id)
        }
    }

    @Test
    fun `Should be able to mark todo as completed`(){
        val id = existingTodos.first().id.toHexString()
        val todo = service.markAsCompleted(id)
        assertInstanceOf<TodoResponse>(todo)
        verify(exactly = 1) { repository.findById(ObjectId(id)) }
        assertEquals(id, todo.id)
        assertEquals(true, todo.completed)
        assertInstanceOf<Instant>(todo.completedAt)
    }

    @Test
    fun `Should throw an error on mark todo as completed if todo doesn't exists`(){
        val id = ObjectId().toHexString()
        try{
            service.markAsCompleted(id)
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            verify(exactly = 1) { repository.findById(ObjectId(id)) }
        }
    }

    @Test
    fun `Should be able to mark todo as uncompleted`(){
        val id = existingTodos.first().id.toHexString()
        val todo = service.markAsUncompleted(id)
        assertInstanceOf<TodoResponse>(todo)
        verify(exactly = 1) { repository.findById(ObjectId(id)) }
        assertEquals(id, todo.id)
        assertEquals(false, todo.completed)
        assertNull(todo.completedAt)
    }

    @Test
    fun `Should throw an error on mark todo as uncompleted if todo doesn't exists`(){
        val id = ObjectId().toHexString()
        try{
            service.markAsUncompleted(id)
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            verify(exactly = 1) { repository.findById(ObjectId(id)) }
        }
    }

}