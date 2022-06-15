package com.mevron.rides.rider.util

import kotlinx.coroutines.CoroutineExceptionHandler

object AppErrorHandler {
    val handler = CoroutineExceptionHandler { _, throwable ->
        println("ERROR: $throwable")

    }
}