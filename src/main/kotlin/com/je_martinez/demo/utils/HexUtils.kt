package com.je_martinez.demo.utils

object HexUtils {
    fun isValidHexString(value: String?): Boolean {
        if (value == null) return false
        return value.matches(Regex("^[a-fA-F0-9]{24}$"))
    }
}