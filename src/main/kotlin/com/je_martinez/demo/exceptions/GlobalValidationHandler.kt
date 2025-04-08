package com.je_martinez.demo.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
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

        val response = mapOf(
            "message" to "Validation failed",
            "errors" to errors)

        return ResponseEntity
            .status(400)
            .body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {

        val rootCause = e.rootCause?.message ?: e.message ?: "Invalid request"

        val errorMessage = when {
            rootCause.contains("Cannot construct instance of") -> {
                "Request body is invalid or required fields are missing."
            }
            rootCause.contains("parameter") && rootCause.contains("null") -> {
                val fieldRegex = Regex("parameter ([a-zA-Z0-9_]+)")
                val fieldName = fieldRegex.find(rootCause)?.groupValues?.get(1) ?: "unknown"
                "Field '$fieldName' must not be null."
            }
            else -> "Malformed JSON request."
        }

        val response = mapOf(
            "message" to "Validation failed",
            "errors" to listOf(errorMessage),
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
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

        return ResponseEntity.status(400).body(response)
    }

}