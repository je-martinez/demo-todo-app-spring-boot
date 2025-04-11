package com.je_martinez.demo.validators

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [HexStringValidator::class])
annotation class HexString(
    val message: String = "Invalid path variable value. Needs to be an Hex String of 24 characters.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)