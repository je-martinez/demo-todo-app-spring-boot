package com.je_martinez.demo.features.authentication.controllers

import com.je_martinez.demo.features.authentication.dtos.requests.AuthRequest
import com.je_martinez.demo.features.authentication.dtos.requests.RefreshRequest
import com.je_martinez.demo.features.authentication.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Controller for authentication operations")
class AuthenticationController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user endpoint")
    fun register(@Valid @RequestBody body: AuthRequest) = authService.register(body.email, body.password)

    @PostMapping("/login")
    @Operation(summary = "Log in endpoint")
    fun login(@Valid @RequestBody body: AuthRequest) = authService.login(body.email, body.password)

    @PostMapping("/refresh")
    @Operation(summary = "Refresh user endpoint")
    fun refresh(@Valid @RequestBody body: RefreshRequest) = authService.refresh(body.refreshToken)
}