package com.je_martinez.demo

import com.je_martinez.demo.controllers.AuthenticationController
import com.je_martinez.demo.controllers.TodoController
import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.security.AuthService
import com.je_martinez.demo.security.JwtService
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest
open class ApplicationDefinitionTests {
    @MockkBean
    private lateinit var authService: AuthService

    @MockkBean
    private lateinit var jwtService: JwtService

    @MockkBean
    private lateinit var todoRepository: TodoRepository

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    private lateinit var authController: AuthenticationController

    @Autowired
    private lateinit var todoController: TodoController
}