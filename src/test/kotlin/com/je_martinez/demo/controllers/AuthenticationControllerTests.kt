package com.je_martinez.demo.controllers

import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.dtos.authentication.AuthRequest
import com.je_martinez.demo.dtos.authentication.RefreshRequest
import com.je_martinez.demo.features.authentication.AuthService
import com.je_martinez.demo.utils.HashEncoder
import com.je_martinez.demo.utils.JwtUtils
import io.jsonwebtoken.security.Keys
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import java.util.Date

class AuthenticationControllerTests: ApplicationDefinitionTests() {

	private val keyAsString = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean enim erat, accumsan id lacus rhoncus."
	private val secretKey =  Keys.hmacShaKeyFor(keyAsString.encodeToByteArray())

	private fun generateTestToken(
		claims: Map<String, Any>,
	): String {
		return JwtUtils.generateToken(
			secretKey,
			subject = mockUserIdAsString,
			issuedAt = Date(),
			expiration = Date(),
			claims,
		)
	}

	private fun generateTokenPair(): AuthService.TokenPair{
		return AuthService.TokenPair(
			accessToken = generateTestToken(
				claims = mapOf(
					"type" to "accessToken",
				)
			),
			refreshToken = generateTestToken(
				claims = mapOf(
					"type" to "refreshToken",
				)
			)
		)
	}

	@Test
	fun `Create User - Test`(){
		val requestBody = AuthRequest(
			email = "test@test.com",
			password = "Testpassword123!"
		)

		val resultUser = User(
			id = mockUserId,
			email = requestBody.email,
			hashedPassword = HashEncoder.encode(requestBody.password)
		)

		every {
			authService.register(requestBody.email, requestBody.password)
		} returns resultUser

		authController.register(requestBody)

		verify(exactly = 1) {
			authService.register(requestBody.email, requestBody.password)
		}
	}

	@Test
	fun `Login - Test`() {

		val requestBody = AuthRequest(
			email = "test@test.com",
			password = "Testpassword123!"
		)

		every {
			authService.login(requestBody.email, requestBody.password)
		} returns generateTokenPair()


		val result = authController.login(requestBody)

		assertInstanceOf<AuthService.TokenPair>(result)

		verify(exactly = 1) {
			authService.login(requestBody.email, requestBody.password)
		}
	}

	@Test
	fun `Refresh Token - Test`() {

		val refreshToken = generateTestToken(
			claims = mapOf(
				"type" to "refreshToken",
			)
		)

		every {
			authService.refresh(refreshToken)
		} returns generateTokenPair()

		val requestBody = RefreshRequest(
			refreshToken
		)

		val result = authController.refresh(requestBody)

		assertInstanceOf<AuthService.TokenPair>(result)

		verify(exactly = 1) {
			authService.refresh(refreshToken)
		}
	}

}
