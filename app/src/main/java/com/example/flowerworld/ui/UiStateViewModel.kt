package com.example.flowerworld.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Base for a ViewModel that owns one [UiState] and changes it only from inside. */
abstract class UiStateViewModel<UiState>(initialState: UiState) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected val currentState: UiState
        get() = _uiState.value

    protected fun updateState(transform: UiState.() -> UiState) = _uiState.update { it.transform() }
}
