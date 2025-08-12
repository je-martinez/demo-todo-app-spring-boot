package com.je_martinez.demo.utils

import com.je_martinez.demo.database.models.User
import com.je_martinez.demo.features.authentication.utils.HashEncoder

object AuthenticationMockUtils {
    fun generateUsers(size: Int): List<MockUser> {
        val users = mutableListOf<MockUser>()
        for (i in 0 until size) {
            val rawPassword = "Test-$i-password!12345"
            val user = User(
                email = "test$i@test.com",
                hashedPassword = HashEncoder.encode(rawPassword),
            )
            users.add(MockUser(
                rawPassword = rawPassword,
                user = user
            ))
        }
        return users
    }
}

