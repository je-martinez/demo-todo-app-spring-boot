package com.je_martinez.demo.features.authentication.utils

import java.security.MessageDigest
import java.util.Base64

object TokenUtils {
    fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}