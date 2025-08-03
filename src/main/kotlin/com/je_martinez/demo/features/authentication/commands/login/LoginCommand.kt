package com.je_martinez.demo.features.authentication.commands.login

import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.trendyol.kediatr.Request

class LoginCommand(
    val email: String,
    val password: String
) : Request<Tokens>