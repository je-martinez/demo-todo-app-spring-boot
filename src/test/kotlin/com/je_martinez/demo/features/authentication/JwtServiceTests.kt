package com.je_martinez.demo.features.authentication

import com.je_martinez.demo.exceptions.TokenExceptions
import com.je_martinez.demo.utils.JwtUtils
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class JwtServiceTests {

    @Autowired
    private lateinit var jwtService: JwtService

    @SpykBean
    private lateinit var service: JwtService

    private val mockUser = ObjectId()
    private val mockUserAsString = mockUser.toHexString()

    @Test
    fun `Should be able generate an access token and refresh token`(){
        val accessToken = service.generateAccessToken(mockUserAsString)
        val refreshToken = service.generateRefreshToken(mockUserAsString)
        assertInstanceOf<String>(accessToken)
        assertInstanceOf<String>(refreshToken)
        verify(exactly = 1) { service.generateAccessToken(mockUserAsString)  }
        verify(exactly = 1) { service.generateRefreshToken(mockUserAsString)  }
    }

    @Test
    fun `Should be able to validate an access token and refresh token`(){
        val accessToken = service.generateAccessToken(mockUserAsString)
        val refreshToken = service.generateRefreshToken(mockUserAsString)

        val validateAccessToken = service.validateAccessToken(accessToken)
        assertInstanceOf<Boolean>(validateAccessToken)
        assertEquals(true, validateAccessToken)
        verify(exactly = 1) { service.validateAccessToken(accessToken)  }

        val validateRefreshToken = service.validateRefreshToken(refreshToken)
        assertInstanceOf<Boolean>(validateRefreshToken)
        assertEquals(true, validateRefreshToken)
        verify(exactly = 1) { service.validateRefreshToken(refreshToken)  }
    }

    @Test
    fun `Should be able to validate an access token and refresh token with Bearer prefix`(){
        val accessToken = "Bearer ${service.generateAccessToken(mockUserAsString)}"
        val refreshToken = "Bearer ${service.generateRefreshToken(mockUserAsString)}"

        val validateAccessToken = service.validateAccessToken(accessToken)
        assertInstanceOf<Boolean>(validateAccessToken)
        assertEquals(true, validateAccessToken)
        verify(exactly = 1) { service.validateAccessToken(accessToken)  }

        val validateRefreshToken = service.validateRefreshToken(refreshToken)
        assertInstanceOf<Boolean>(validateRefreshToken)
        assertEquals(true, validateRefreshToken)
        verify(exactly = 1) { service.validateRefreshToken(refreshToken)  }
    }

    @Test
    fun `Should return false if access token or refresh token are invalid`(){
        val accessToken = "an-invalid-token"
        val refreshToken = "an-invalid-refresh-token"
        val validateAccessToken = service.validateAccessToken(accessToken)
        assertInstanceOf<Boolean>(validateAccessToken)
        assertEquals(false, validateAccessToken)
        verify(exactly = 1) { service.validateAccessToken(accessToken)  }

        val validateRefreshToken = service.validateRefreshToken(refreshToken)
        assertInstanceOf<Boolean>(validateRefreshToken)
        assertEquals(false, validateRefreshToken)
        verify(exactly = 1) { service.validateRefreshToken(refreshToken)  }
    }

    @Test
    fun `Should return false if type claim doesn't exist on access and refresh token`(){
        val tokenWithNoClaims = JwtUtils.generateToken(
            secretKey = jwtService.getSecret(),
            issuedAt = Date(),
            expiration = Date.from(Instant.now().plus(1, ChronoUnit.HOURS)),
            subject = mockUserAsString
        )

        val validateAccessToken = service.validateAccessToken(tokenWithNoClaims)
        assertInstanceOf<Boolean>(validateAccessToken)
        assertEquals(false, validateAccessToken)
        verify(exactly = 1) { service.validateAccessToken(tokenWithNoClaims)  }

        val validateRefreshToken = service.validateRefreshToken(tokenWithNoClaims)
        assertInstanceOf<Boolean>(validateRefreshToken)
        assertEquals(false, validateRefreshToken)
        verify(exactly = 1) { service.validateRefreshToken(tokenWithNoClaims)  }
    }

    @Test
    fun `Should extract the subject from the token`(){

        val accessToken = service.generateAccessToken(mockUserAsString)
        val refreshToken = service.generateRefreshToken(mockUserAsString)

        val subjectAccessToken = jwtService.getUserIdFromToken(accessToken)
        val subjectRefreshToken = jwtService.getUserIdFromToken(refreshToken)

        assertInstanceOf<String>(subjectAccessToken)
        assertInstanceOf<String>(subjectRefreshToken)

        assertEquals(mockUserAsString, subjectAccessToken)
        assertEquals(mockUserAsString, subjectRefreshToken)
    }

    @Test
    fun `Should throw an error on extract the subject if the token is not valid`(){

        val accessToken = "invalid-token"
        val refreshToken = "invalid-token"

        try{
            jwtService.getUserIdFromToken(accessToken)
            throw Exception("Method above should throw an error")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every { jwtService.getUserIdFromToken(accessToken) } throws TokenExceptions.invalidToken()
        }

        try{
            jwtService.getUserIdFromToken(refreshToken)
            throw Exception("Method above should throw an error")
        }catch (e: Throwable){
            assertInstanceOf<ResponseStatusException>(e)
            every { jwtService.getUserIdFromToken(refreshToken) } throws TokenExceptions.invalidToken()
        }
    }

}