package com.mevron.rides.rider.shared.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<State, Event> : ViewModel() {

    abstract fun createInitialState(): State

    private val initialState by lazy { createInitialState() }

    private val mutableState: MutableStateFlow<State> = MutableStateFlow(initialState)

    val uiState: StateFlow<State>
        get() = mutableState

    fun setState(reduce: State.() -> State) {
        mutableState.value = uiState.value.reduce()
    }

    open fun setEvent(event: Event) {}
}

class SingleStateEvent<T> {

    @Volatile
    var data: T? = null

    @Synchronized
    fun get(collector: (T) -> Unit) {
        if (data == null) return
        val current = data

        if (current != null) {
            collector(current)
            data = null
        }
    }

    @Synchronized
    fun set(data: T) {
       this.data = data
    }

    @Synchronized
    fun hasValue() = data != null
}