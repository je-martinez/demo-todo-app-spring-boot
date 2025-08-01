package com.je_martinez.demo.utils

import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

object LoggerUtils{
    fun <R : Any> R.logger(): Lazy<org.slf4j.Logger> {
        return lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }
    }

    private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
        return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
            ofClass.enclosingClass
        } else {
            ofClass
        }
    }
}
