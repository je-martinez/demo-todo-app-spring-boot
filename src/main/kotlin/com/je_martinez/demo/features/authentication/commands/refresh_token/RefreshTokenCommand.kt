package com.je_martinez.demo.features.authentication.commands.refresh_token

import com.je_martinez.demo.features.authentication.dtos.shared.Tokens
import com.trendyol.kediatr.Request

class RefreshTokenCommand(
    val refreshToken: String
) : Request<Tokens>