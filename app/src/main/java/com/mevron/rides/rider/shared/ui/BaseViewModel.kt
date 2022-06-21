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

data class Test(val item: String)