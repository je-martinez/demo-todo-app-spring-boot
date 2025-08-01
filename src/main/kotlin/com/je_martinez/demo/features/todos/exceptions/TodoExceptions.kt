package com.je_martinez.demo.features.todos.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

object TodoExceptions {
    fun notFound (id:String): ResponseStatusException =
        ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with $id not found")
    fun forbidden(): ResponseStatusException =
        ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this resource")
}