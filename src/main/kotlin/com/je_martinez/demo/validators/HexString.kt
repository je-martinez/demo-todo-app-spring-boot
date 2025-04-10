package com.je_martinez.demo.validators

import com.je_martinez.demo.utils.HexUtils
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [HexStringValidator::class])
annotation class HexString(
    val message: String = "Invalid path variable value. Needs to be an Hex String of 24 characters.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class HexStringValidator : ConstraintValidator<HexString, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean = HexUtils.isValidHexString(value)
}