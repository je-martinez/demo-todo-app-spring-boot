package com.je_martinez.demo.utils

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync

object KediatrUtils {
    fun <T> wrapMediatorExecution(command: suspend () -> T): CompletableFuture<T> {
        return supplyAsync {
            kotlinx.coroutines.runBlocking {
                command()
            }
        }
    }
}