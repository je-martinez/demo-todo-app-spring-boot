package com.je_martinez.demo.controllers

import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.AuthService
import com.je_martinez.demo.features.authentication.JwtService
import com.je_martinez.demo.features.todos.TodoService
import com.ninjasquad.springmockk.MockkBean
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest
open class ApplicationDefinitionTests {

    val mockUserId: ObjectId = ObjectId()
    val mockUserIdAsString: String = mockUserId.toHexString()

    @MockkBean
    lateinit var authService: AuthService

    @MockkBean
    lateinit var jwtService: JwtService

    @MockkBean
    lateinit var todoService: TodoService

    @MockkBean
    lateinit var todoRepository: TodoRepository

    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    lateinit var authController: AuthenticationController

    @Autowired
    lateinit var todoController: TodoController
}