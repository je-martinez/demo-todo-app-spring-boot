package com.je_martinez.demo.controllers

import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
import kotlin.test.*

class AuthenticationControllerRestTests: ApplicationDefinitionRestTests() {

    @Test
    fun `Should return 400 if body isn't provided in the request`(){

        val entity = HttpEntity(null, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val emptyBody = HttpEntity("{}", jsonHeaders)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/register",
            HttpMethod.POST,
            emptyBody,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 400 on register if body provided is non-valid`(){

        val fBody = mapOf("email" to "wrong-email")

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val sBody = mapOf("email" to "test@example.com", "password" to "weak-pass")

        val sEntity = HttpEntity(sBody, jsonHeaders)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/register",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 200 if register credentials are valid`(){

        val fBody = mapOf(
            "email" to "test@example.com",
            "password" to "TestPassword1234$"
        )

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.CREATED, fResponse.statusCode)
    }

    @Test
    fun `Should return 400 on login if body provided is non-valid`(){

        val fBody = mapOf("email" to "wrong-email")

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val sBody = mapOf("email" to "test@example.com", "password" to "weak-pass")

        val sEntity = HttpEntity(sBody, jsonHeaders)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/login",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 200 on login if credentials are valid`(){


        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword
        )

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.OK, fResponse.statusCode)
    }

    @Test
    fun `Should return 401 on login if credentials are non-valid`(){

        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword+"invalid-string"
        )

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, fResponse.statusCode)
    }

    @Test
    fun `Should return 401 on login if refresh token is non-valid`(){

        val fBody = mapOf(
            "refreshToken" to "invalid-refresh-token"
        )

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/refresh",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, fResponse.statusCode)
    }

    @Test
    fun `Should return 201 on refresh if refresh token is valid`(){

        val tokens = login()

        val sBody = mapOf(
            "refreshToken" to tokens.refreshToken,
        )

        val sEntity = HttpEntity(sBody, jsonHeaders)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$authBaseUrl/refresh",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.OK, sResponse.statusCode)
    }


}