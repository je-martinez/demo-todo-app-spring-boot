package com.je_martinez.demo.controllers

import com.je_martinez.demo.dtos.auth.AuthRequest
import com.je_martinez.demo.dtos.auth.RefreshRequest
import com.je_martinez.demo.features.authentication.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody body: AuthRequest){ authService.register(body.email, body.password) }

    @PostMapping("/login")
    fun login(@Valid @RequestBody body: AuthRequest) = authService.login(body.email, body.password)

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody body: RefreshRequest) = authService.refresh(body.refreshToken)
}