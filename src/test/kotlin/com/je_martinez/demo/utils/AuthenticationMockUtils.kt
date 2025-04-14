package com.je_martinez.demo.utils

import com.je_martinez.demo.database.models.User

object AuthenticationMockUtils {
    fun generateUsers(size: Int): List<User> {
        val users = mutableListOf<User>()
        for (i in 0 until size) {
            val user = User(
                email = "test$i@test.com",
                hashedPassword = HashEncoder.encode("Test-$i-password!12345"),
            )
            users.add(user)
        }
        return users
    }
}