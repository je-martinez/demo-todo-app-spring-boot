package com.je_martinez.demo.features.authentication.controllers

import com.je_martinez.demo.features.authentication.commands.login.LoginCommand
import com.je_martinez.demo.features.authentication.commands.refresh_token.RefreshTokenCommand
import com.je_martinez.demo.features.authentication.commands.register.RegisterCommand
import com.je_martinez.demo.features.authentication.dtos.requests.AuthRequest
import com.je_martinez.demo.features.authentication.dtos.requests.RefreshRequest
import com.je_martinez.demo.features.authentication.dtos.responses.RegisterResponse
import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.je_martinez.demo.features.authentication.services.AuthService
import com.je_martinez.demo.utils.KediatrUtils
import com.trendyol.kediatr.Mediator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Controller for authentication operations")
class AuthenticationController(
    private val mediator: Mediator,
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user endpoint")
    fun register(@Valid @RequestBody body: AuthRequest): CompletableFuture<RegisterResponse>? {
        return KediatrUtils.wrapMediatorExecution {
            mediator.send(RegisterCommand(body.email, body.password))
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Log in endpoint")
    fun login(@Valid @RequestBody body: AuthRequest): CompletableFuture<Tokens> {
        return KediatrUtils.wrapMediatorExecution {
            mediator.send(LoginCommand(body.email, body.password))
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh user endpoint")
    fun refresh(@Valid @RequestBody body: RefreshRequest): CompletableFuture<Tokens> {
        return KediatrUtils.wrapMediatorExecution {
            mediator.send(RefreshTokenCommand(body.refreshToken))
        }
    }
}