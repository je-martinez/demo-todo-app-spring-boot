package com.je_martinez.demo.controllers

import com.je_martinez.demo.dtos.todos.TodoRequest
import com.je_martinez.demo.dtos.todos.TodoResponse
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TodoControllerRestTests:ApplicationDefinitionRestTests() {

    @Test
    fun `Should return 401 on get all if token is not provided in the request`(){
        val response = template.exchange<List<TodoResponse>>(
            todosBaseUrl,
            HttpMethod.GET,
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 200 on get all if token is provided and is valid in the request`(){
        val tokens = login()

        val response = template.exchange<List<TodoResponse>>(
            todosBaseUrl,
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertNotNull(response.body)
        assertInstanceOf<List<TodoResponse>>(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.size, todoRepository.count().toInt())
    }

    @Test
    fun `Should return 401 on get all by owner if token is not provided in the request`(){
        val response = template.exchange<List<TodoResponse>>(
            "$todosBaseUrl/by-owner",
            HttpMethod.GET,
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 200 on get all by owner if token is provided and is valid in the request`(){
        val tokens = login()

        val userId = jwtService.getUserIdFromToken(tokens.accessToken)

        val response = template.exchange<List<TodoResponse>>(
            "$todosBaseUrl/by-owner",
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken),
        )
        assertNotNull(response.body)
        assertInstanceOf<List<TodoResponse>>(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertInstanceOf<Int>(response.body?.size)
        response.body?.forEach{
            assertInstanceOf<TodoResponse>(it)
            assertEquals(it.ownerId, userId)
        }
    }

    @Test
    fun `Should return 403 if user doesn't have ownership of the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val notOwnershipTodos = existingTodos.filter {
            it.ownerId.toHexString() != userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/${notOwnershipTodos.first().id.toHexString()}",
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken),
        )
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `Should return 404 if user doesn't have ownership of the todo`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/${ObjectId().toHexString()}",
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken),
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Should return 400 if id is not a valid hex string`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/123",
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken),
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 if user has ownership of the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val myTodos = existingTodos.filter {
            it.ownerId.toHexString() == userId
        }
        val response = template.exchange<TodoResponse>(
            "$todosBaseUrl/${myTodos.first().id.toHexString()}",
            HttpMethod.GET,
            getAuthorizedRequest(null, tokens.accessToken),
        )
        assertNotNull(response.body)
        assertInstanceOf<TodoResponse>(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.ownerId, userId)
    }

    @Test
    fun `Should return 401 on create if token is not provided in the request`(){
        val body = TodoRequest(
            title = "New title",
            description = "New description",
        )
        val request = HttpEntity(body)
        val response = template.exchange<List<TodoResponse>>(
            todosBaseUrl,
            HttpMethod.POST,
            request
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 400 on create if body provided doesn't have a title`(){
        val tokens = login()
        val response = template.exchange<Void>(
            todosBaseUrl,
            HttpMethod.POST,
            getAuthorizedRequest(mapOf(
                "description" to "New Description!",
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 400 on create if body provided has nullable properties`(){
        val tokens = login()
        val title = null
        val description =  "New Description!"
        val response = template.exchange<Void>(
            todosBaseUrl,
            HttpMethod.POST,
            getAuthorizedRequest(mapOf(
                "title" to title,
                "description" to description,
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 on create if body provided is valid`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val title ="New Title!"
        val description =  "New Description!"
        val response = template.exchange<TodoResponse>(
            todosBaseUrl,
            HttpMethod.POST,
            getAuthorizedRequest(mapOf(
                "title" to title,
                "description" to description,
            ), tokens.accessToken)
        )
        assertNotNull(response.body)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertInstanceOf<TodoResponse>(response.body)
        assertEquals(response.body?.ownerId, userId)
        assertEquals(response.body?.title, title)
        assertEquals(response.body?.description, description)
    }

    @Test
    fun `Should return 401 on update if token is not provided`(){
        val body = TodoRequest(
            title = "Updated title",
            description = "Updated description",
        )
        val todoToUpdate = existingTodos.first()
        val request = HttpEntity(body)
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.PUT,
            request
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 400 on update if wrong body is provided`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first {
            it.ownerId.toHexString() == userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "description" to "Updated Description!",
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 403 on update if user don't owned the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() != userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "title" to "Updated Title!",
                "description" to "Updated Description!",
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `Should return 404 on update if a todo doesn't exists`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/${ObjectId().toHexString()}",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "title" to "Updated Title!",
                "description" to "Updated Description!",
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Should return 400 on update if id is not a valid hex string`(){
        val tokens = login()
        val title = "Updated Title!"
        val description = "Updated Description!"
        val response = template.exchange<Void>(
            "$todosBaseUrl/123",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "title" to title,
                "description" to description,
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 400 on update if a valid body has nullable properties`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() == userId
        }
        val title = null
        val description = "Updated Description!"
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "title" to title,
                "description" to description,
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 on update if a valid body is provided`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() == userId
        }
        val title = "Updated Title!"
        val description = "Updated Description!"
        val response = template.exchange<TodoResponse>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.PUT,
            getAuthorizedRequest(mapOf(
                "title" to title,
                "description" to description,
            ), tokens.accessToken)
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertInstanceOf<TodoResponse>(response.body)
        assertEquals(response.body?.title, title)
        assertEquals(response.body?.description, description)
        assertEquals(response.body?.ownerId, userId)
    }

    @Test
    fun `Should return 401 on mark as completed if token is not provided`(){
        val todoToUpdate = existingTodos.first()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-completed/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 403 on mark as completed if user don't owned the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() != userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-completed/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `Should return 404 on mark as completed if todo doesn't exists`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-completed/${ObjectId().toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Should return 400 on mark as completed if id is not a valid hex string`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-completed/123",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 on mark as completed update if a valid body is provided`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() == userId
        }
        val response = template.exchange<TodoResponse>(
            "$todosBaseUrl/mark-as-completed/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertInstanceOf<TodoResponse>(response.body)
        assertEquals(response.body?.ownerId, userId)
        assertEquals(true, response.body?.completed)
        assertInstanceOf<Instant>(response.body?.completedAt)
    }

    @Test
    fun `Should return 401 on mark as uncompleted if token is not provided`(){
        val todoToUpdate = existingTodos.first()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-uncompleted/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 403 on mark as uncompleted if user don't owned the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() != userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-uncompleted/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `Should return 404 on mark as uncompleted if todo doesn't exists`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-uncompleted/${ObjectId().toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Should return 400 on mark as uncompleted if id is not a valid hex string`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/mark-as-uncompleted/123",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 on mark as uncompleted if a valid body is provided`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() == userId
        }
        val response = template.exchange<TodoResponse>(
            "$todosBaseUrl/mark-as-uncompleted/${todoToUpdate.id.toHexString()}",
            HttpMethod.PATCH,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertInstanceOf<TodoResponse>(response.body)
        assertEquals(response.body?.ownerId, userId)
        assertEquals(false, response.body?.completed)
        assertNull(response.body?.completedAt)
    }

    @Test
    fun `Should return 401 on delete if token is not provided`(){
        val todoToUpdate = existingTodos.first()
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.DELETE,
        )
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `Should return 403 on delete if user don't owned the todo`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() != userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.DELETE,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `Should return 404 on delete if todo doesn't exists`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/${ObjectId().toHexString()}",
            HttpMethod.DELETE,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Should return 400 on delete if id is not a valid hex string`(){
        val tokens = login()
        val response = template.exchange<Void>(
            "$todosBaseUrl/123",
            HttpMethod.DELETE,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return 200 on delete if a valid body is provided`(){
        val tokens = login()
        val userId = jwtService.getUserIdFromToken(tokens.accessToken)
        val todoToUpdate = existingTodos.first{
            it.ownerId.toHexString() == userId
        }
        val response = template.exchange<Void>(
            "$todosBaseUrl/${todoToUpdate.id.toHexString()}",
            HttpMethod.DELETE,
            getAuthorizedRequest(null, tokens.accessToken)
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}