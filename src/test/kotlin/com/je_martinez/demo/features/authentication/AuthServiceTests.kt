package com.je_martinez.demo.features.authentication

import com.je_martinez.demo.database.models.RefreshToken
import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.database.repository.RefreshTokenRepository
import com.je_martinez.demo.database.repository.UserRepository
import com.je_martinez.demo.exceptions.AuthExceptions
import com.je_martinez.demo.utils.AuthenticationMockUtils
import com.ninjasquad.springmockk.SpykBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
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

    @BeforeTest
    fun testSetup() {
        existingUsers.addAll(AuthenticationMockUtils.generateUsers(5))
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
        val password = "Test-0-password!12345"

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
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every {
                service.login(user.email, password)
            } throws AuthExceptions.invalidCredentials()
        }
    }
}