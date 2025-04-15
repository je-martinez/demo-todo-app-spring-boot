package com.je_martinez.demo.controllers

import com.je_martinez.demo.dtos.todos.TodoResponse
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
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

        val allHeaders = jsonHeaders.apply {
            add("Authorization", "Bearer ${tokens.accessToken}")
        }

        val request = HttpEntity(null, allHeaders)

        val response = template.exchange<List<TodoResponse>>(
            todosBaseUrl,
            HttpMethod.GET,
            request
        )
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

        val allHeaders = jsonHeaders.apply {
            add("Authorization", "Bearer ${tokens.accessToken}")
        }

        val request = HttpEntity(null, allHeaders)

        val response = template.exchange<List<TodoResponse>>(
            "$todosBaseUrl/by-owner",
            HttpMethod.GET,
            request
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertInstanceOf<Int>(response.body?.size)
        assertNotNull(response.body)
        response.body?.forEach{
            assertInstanceOf<TodoResponse>(it)
            assertEquals(it.ownerId, userId)
        }
    }

}