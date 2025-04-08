package com.je_martinez.demo

import jakarta.validation.ConstraintViolationException
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        }
        return ResponseEntity
            .status(400)
            .body(mapOf("errors" to errors))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleValidationError(e: NotFoundException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity
            .status(404)
            .body(mapOf("message" to e.message.toString()))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errors = ex.constraintViolations.associate { violation ->
            val field = violation.propertyPath.toString().split(".").last()
            field to violation.message
        }

        val response = mapOf(
            "message" to "Validation failed",
            "errors" to errors
        )

        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

}