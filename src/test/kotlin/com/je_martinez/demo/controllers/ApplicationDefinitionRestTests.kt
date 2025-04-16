package com.je_martinez.demo.controllers

import com.je_martinez.demo.database.models.Todo
import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.AuthService
import com.je_martinez.demo.features.authentication.JwtService
import com.je_martinez.demo.utils.AuthenticationMockUtils
import com.je_martinez.demo.utils.MockUser
import com.je_martinez.demo.utils.TodoMockUtils
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
    lateinit var todoRepository: TodoRepository

    @SpykBean
    lateinit var userRepository: UserRepository

    @SpykBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @SpykBean
    lateinit var jwtService: JwtService

    var existingUsers = mutableListOf<MockUser>()

    var existingTodos = mutableListOf<Todo>()

    private val numberOfUsers = 5
    private val numberOfTodos = 5
    private val numberOfOwnerTodos = 5

    @BeforeTest
    fun setup(){
        val usersCreated = AuthenticationMockUtils.generateUsers(numberOfUsers)
        userRepository.saveAll(usersCreated.map{it.user})
        existingUsers.addAll(usersCreated)
        val existingUserId = usersCreated.first().user.id
        val newTodos = TodoMockUtils.generateMockData(numberOfTodos)
        val newTodosOwner = TodoMockUtils.generateMockData(numberOfOwnerTodos, existingUserId)
        todoRepository.saveAll(newTodos + newTodosOwner)
        existingTodos.addAll(newTodos + newTodosOwner)
    }

    @AfterTest
    fun tearDown() {
        userRepository.deleteAll()
        refreshTokenRepository.deleteAll()
        todoRepository.deleteAll()
    }

    fun login(userIndex: Int = 0): AuthService.TokenPair{
        val user = existingUsers.elementAt(userIndex)

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

        return fResponse.body ?: throw Exception("Login failed on ApplicationDefinitionRestTests")
    }

    fun getAuthorizedHeaders(accessToken:String?, userIndex:Int = 0): HttpHeaders {
        val token = accessToken ?: login(userIndex).accessToken
        return jsonHeaders.apply {
            add("Authorization", "Bearer $token")
        }
    }

    fun getAuthorizedRequest(body:Map<String, Any>?, accessToken: String?):HttpEntity<Any>{
        return HttpEntity(body, getAuthorizedHeaders(accessToken))
    }

}