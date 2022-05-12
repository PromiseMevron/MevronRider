package com.mevron.rides.rider.domain

import kotlinx.coroutines.flow.MutableStateFlow

fun<T> MutableStateFlow<T>.update(func: () -> T) {
    this.value = func()
}