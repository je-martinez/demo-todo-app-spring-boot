package com.je_martinez.demo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TodoExceptions {
    companion object{
        fun notFoundException (id:String): ResponseStatusException {
            return ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with $id not found")
        }
    }
}