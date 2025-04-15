package com.je_martinez.demo.utils

import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.assertEquals

class HexUtilsTests {

    @Test
    fun  `isValidHexString (true) - Test`(){
        val result = HexUtils.isValidHexString(ObjectId().toHexString())
        assertInstanceOf<Boolean>(result)
        assertEquals(true, result)
    }

    @Test
    fun  `isValidHexString (false) - Test`(){
        val result = HexUtils.isValidHexString("a-not-valid-test")
        assertInstanceOf<Boolean>(result)
        assertEquals(false, result)
    }

    @Test
    fun  `isValidHexString nullable (false) - Test`(){
        val result = HexUtils.isValidHexString(null)
        assertInstanceOf<Boolean>(result)
        assertEquals(false, result)
    }


}