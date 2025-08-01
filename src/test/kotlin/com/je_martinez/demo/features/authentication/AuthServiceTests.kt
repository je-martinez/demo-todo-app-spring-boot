package com.je_martinez.demo.features.authentication

import com.je_martinez.demo.database.models.RefreshToken
import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.features.authentication.exceptions.AuthExceptions
import com.je_martinez.demo.features.authentication.exceptions.TokenExceptions
import com.je_martinez.demo.features.authentication.services.AuthService
import com.je_martinez.demo.features.authentication.services.JwtService
import com.je_martinez.demo.utils.AuthenticationMockUtils
import com.ninjasquad.springmockk.SpykBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@SpringBootTest
class AuthServiceTests {
    @SpykBean
    private lateinit var jwtService: JwtService

    @SpykBean
    private lateinit var userRepository: UserRepository

    @SpykBean
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @SpykBean
    private lateinit var service: AuthService

    private var existingUsers = mutableListOf<User>()

    private var rawUserPasswords = mutableListOf<String>()

    @BeforeTest
    fun testSetup() {
        val users = AuthenticationMockUtils.generateUsers(5)
        existingUsers.addAll(users.map { it.user })
        rawUserPasswords.addAll(users.map { it.rawPassword })
        userRepository.saveAll(existingUsers)
    }

    @AfterTest
    fun teardown() {
        userRepository.deleteAll()
        refreshTokenRepository.deleteAll()
    }

    @BeforeEach
    fun reset(){
        clearAllMocks()
    }

    @Test
    fun `Should be able to register user`(){

        val newEmail = "new@example.com"
        val newPassword = "newPassword#123"
        val capturedArgs = slot<User>()

        val user = service.register(newEmail, newPassword)

        assertInstanceOf<User>(user)

        verify(exactly = 1) { userRepository.findByEmail(newEmail) }

        verify(exactly = 1) { userRepository.save(capture(capturedArgs)) }

        assertEquals(user, capturedArgs.captured)
    }

    @Test
    fun `Should throw an error if user already exists`(){

        val emailAlreadyExists = existingUsers.first().email
        val newPassword = "newPassword#123"

        try {
            service.register(emailAlreadyExists, newPassword)
            throw Exception("Method should failed before this point")
        } catch(e:Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.register(emailAlreadyExists, newPassword)
            } throws AuthExceptions.userAlreadyExists(emailAlreadyExists)
        }
    }

    @Test
    fun `Should be able to login`(){
        val user = existingUsers.first()
        val password = rawUserPasswords.first()

        val tokens = service.login(user.email, password)

        assertInstanceOf<AuthService.TokenPair>(tokens)

        verify(exactly = 1) {
            userRepository.findByEmail(user.email)
        }

        verify(exactly = 1) {
            jwtService.generateAccessToken(user.id.toHexString())
        }

        verify(exactly = 1) {
            jwtService.generateRefreshToken(user.id.toHexString())
        }

        verify(exactly = 1) {
            refreshTokenRepository.save(any<RefreshToken>())
        }
    }

    @Test
    fun `Should throw an exception if email doesn't exists on the database`(){
        val email = "fake_email@example.com"
        val password = "fakePassword!1234"

        try{
            service.login(email, password)
            throw Exception("Method should failed before this point")
        }catch (e:Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.login(email, password)
            } throws AuthExceptions.invalidCredentials()
        }
    }

    @Test
    fun `Should throw an error on exception on wrong login`(){
        val user = existingUsers.first()
        val password = "AInvalid#12345!"
        try{
            service.login(user.email, password)
            throw Exception("Method should failed before this point")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.login(user.email, password)
            } throws AuthExceptions.invalidCredentials()
        }
    }

    @Test
    fun `Should throw an error on refresh token with an invalid token`(){
        val invalidToken = "a-invalid-token"
        try{
            service.refresh(invalidToken)
            throw Exception("Method should failed before this point")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            verify(exactly = 1) { jwtService.validateRefreshToken(invalidToken) }
            every {
                service.refresh(invalidToken)
            } throws TokenExceptions.invalidRefreshToken()
        }
    }

    @Test
    fun `Should throw an error on refresh if user doesn't exist in the database`(){
        val mockUser = ObjectId()
        val refreshToken = jwtService.generateRefreshToken(mockUser.toHexString())
        try{
            service.refresh(refreshToken)
            throw Exception("Method should failed before this point")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.refresh(refreshToken)
            } throws TokenExceptions.invalidRefreshToken()
            verify(exactly = 1) { jwtService.validateRefreshToken(refreshToken) }
            verify(exactly = 1) { userRepository.findById(mockUser) }
            every {
                userRepository.findById(mockUser)
            } throws TokenExceptions.invalidRefreshToken()

        }
    }

    @Test
    fun `Should throw an error if refresh token doesn't exist in the database`(){
        val mockUser = existingUsers.first().id
        val unexistingToken = jwtService.generateRefreshToken(mockUser.toHexString())
        try{
            service.refresh(unexistingToken)
            throw Exception("Method should failed before this point")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.refresh(unexistingToken)
            } throws TokenExceptions.invalidRefreshTokenUsedOrExpired()
            verify(exactly = 1) { jwtService.validateRefreshToken(unexistingToken) }
            verify(exactly = 1) { userRepository.findById(mockUser) }
            every {
                refreshTokenRepository.findByUserIdAndHashedToken(any(), any())
            } throws TokenExceptions.invalidRefreshTokenUsedOrExpired()
        }
    }

    @Test
    fun `Should be able to refresh token`(){
        val userId = existingUsers.first().id
        val email = existingUsers.first().email
        val rawPassword = rawUserPasswords.first()
        val tokens = service.login(email, rawPassword)
        clearAllMocks() // Reset Mocks and Functions Calls
        service.refresh(tokens.refreshToken)
        verify(exactly = 1) { jwtService.validateRefreshToken(tokens.refreshToken) }
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { refreshTokenRepository.findByUserIdAndHashedToken(any(), any()) }
        verify(exactly = 1) { refreshTokenRepository.deleteByUserIdAndHashedToken(any(), any()) }
        verify(exactly = 1){ jwtService.generateAccessToken(userId.toHexString()) }
        verify(exactly = 1){ jwtService.generateRefreshToken(userId.toHexString()) }
        verify(exactly = 1){ refreshTokenRepository.save(any()) }
    }

}