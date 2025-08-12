package com.je_martinez.demo.utils

import com.je_martinez.demo.features.authentication.utils.HashEncoder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.assertEquals

class HashEncoderTests {

    @Test
    fun `Encode Hash should return a String`(){
        val result = HashEncoder.encode("A random string to encode")
        assertInstanceOf<String>(result)
    }

    @Test
    fun `Hash Encoder should return a false if strings doesnt match`(){
        val result = HashEncoder.encode("A random string to encode")

        val matched = HashEncoder.matches("Hello", result)

        assertInstanceOf<Boolean>(matched)
        assertEquals(false, matched)
    }

    @Test
    fun `Hash Encoder should return a true if strings match`(){
        val text = "A random string to encode"
        val result = HashEncoder.encode(text)
        val matched = HashEncoder.matches(text, result)
        assertInstanceOf<Boolean>(matched)
        assertEquals(true, matched)
    }

}