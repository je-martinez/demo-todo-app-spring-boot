package com.je_martinez.demo.controllers

import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.AuthService
import com.je_martinez.demo.utils.AuthenticationMockUtils
import com.je_martinez.demo.utils.MockUser
import com.ninjasquad.springmockk.SpykBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = ["server.port=9055"]
)
class ApplicationDefinitionRestTests {

    val template: TestRestTemplate = TestRestTemplate()
    final val authBaseUrl = "http://localhost:9055/api/auth"
    val todosBaseUrl = "http://localhost:9055/api/todos"
    val jsonHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    @SpykBean
    lateinit var userRepository: UserRepository

    @SpykBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    var existingUsers = mutableListOf<MockUser>()

    @BeforeTest
    fun setup(){
        val usersCreated = AuthenticationMockUtils.generateUsers(1)
        userRepository.saveAll(usersCreated.map{it.user})
        existingUsers.addAll(usersCreated)
    }

    @AfterTest
    fun tearDown() {
        userRepository.deleteAll()
        refreshTokenRepository.deleteAll()
    }

    fun login(): AuthService.TokenPair?{
        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword
        )

        val entity = HttpEntity(fBody, jsonHeaders)

        val fResponse: ResponseEntity<AuthService.TokenPair> = template.exchange(
            "$authBaseUrl/login",
            HttpMethod.POST,
            entity,
        )

        return fResponse.body
    }
}