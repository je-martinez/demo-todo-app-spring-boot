package com.je_martinez.demo.controllers

import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.AuthService.TokenPair
import com.je_martinez.demo.utils.AuthenticationMockUtils
import com.je_martinez.demo.utils.MockUser
import com.ninjasquad.springmockk.SpykBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.*
import kotlin.test.*

@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    properties = ["server.port=9055"]
)
class AuthenticationControllerRestTests {

    val template: TestRestTemplate = TestRestTemplate()
    private val baseURL = "http://localhost:9055/api/auth"

    @SpykBean
    private lateinit var userRepository: UserRepository

    @SpykBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    private var existingUsers = mutableListOf<MockUser>()

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

    @Test
    fun `Should return 400 if body isn't provided in the request`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(null, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val emptyBody = HttpEntity("{}", headers)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/register",
            HttpMethod.POST,
            emptyBody,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 400 on register if body provided is non-valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val fBody = mapOf("email" to "wrong-email")

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val sBody = mapOf("email" to "test@example.com", "password" to "weak-pass")

        val sEntity = HttpEntity(sBody, headers)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/register",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 200 if register credentials are valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val fBody = mapOf(
            "email" to "test@example.com",
            "password" to "TestPassword1234$"
        )

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/register",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.CREATED, fResponse.statusCode)
    }

    @Test
    fun `Should return 400 on login if body provided is non-valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val fBody = mapOf("email" to "wrong-email")

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, fResponse.statusCode)

        val sBody = mapOf("email" to "test@example.com", "password" to "weak-pass")

        val sEntity = HttpEntity(sBody, headers)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/login",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.BAD_REQUEST, sResponse.statusCode)
    }

    @Test
    fun `Should return 200 on login if credentials are valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword
        )

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.OK, fResponse.statusCode)
    }

    @Test
    fun `Should return 401 on login if credentials are non-valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword+"invalid-string"
        )

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/login",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, fResponse.statusCode)
    }

    @Test
    fun `Should return 401 on login if refresh token is non-valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val fBody = mapOf(
            "refreshToken" to "invalid-refresh-token"
        )

        val entity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/refresh",
            HttpMethod.POST,
            entity,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, fResponse.statusCode)
    }

    @Test
    fun `Should return 201 on refresh if refresh token is valid`(){

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val user = existingUsers.first()

        val fBody = mapOf(
            "email" to user.user.email,
            "password" to user.rawPassword
        )

        val fEntity = HttpEntity(fBody, headers)

        val fResponse: ResponseEntity<TokenPair> = template.exchange(
            "$baseURL/login",
            HttpMethod.POST,
            fEntity,
        )

        assertNotNull(fResponse.body)

        val sBody = mapOf(
            "refreshToken" to fResponse.body?.refreshToken,
        )

        val sEntity = HttpEntity(sBody, headers)

        val sResponse: ResponseEntity<Void> = template.exchange(
            "$baseURL/refresh",
            HttpMethod.POST,
            sEntity,
        )

        assertEquals(HttpStatus.OK, sResponse.statusCode)
    }


}