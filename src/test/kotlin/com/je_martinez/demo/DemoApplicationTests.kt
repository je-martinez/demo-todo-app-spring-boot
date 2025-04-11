package com.je_martinez.demo

import com.je_martinez.demo.controllers.AuthenticationController
import com.je_martinez.demo.controllers.TodoController
import com.je_martinez.demo.database.repository.TodoRepository
import com.je_martinez.demo.security.AuthService
import com.je_martinez.demo.security.JwtService
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension


class AuthenticationControllerTests: ApplicationDefinitionTests() {

	@Test
	fun contextLoads() {
	}

}
