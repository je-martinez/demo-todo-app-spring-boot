package com.je_martinez.demo.features.authentication.commands.register

import com.je_martinez.demo.features.authentication.dtos.responses.RegisterResponse
import com.trendyol.kediatr.Request

class RegisterCommand(val email: String, val password: String): Request<RegisterResponse>
