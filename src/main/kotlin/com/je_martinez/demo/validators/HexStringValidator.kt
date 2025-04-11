package com.je_martinez.demo.validators

import com.je_martinez.demo.utils.HexUtils
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class HexStringValidator : ConstraintValidator<HexString, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean = HexUtils.isValidHexString(value)
}