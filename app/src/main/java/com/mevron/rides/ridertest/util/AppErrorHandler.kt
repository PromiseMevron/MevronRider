package com.mevron.rides.ridertest.util

import kotlinx.coroutines.CoroutineExceptionHandler

object AppErrorHandler {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("ERROR: $throwable")

    }
}