package com.example.flowerworld.feature.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    // Index of the selected footer button; the first (leftmost) one is selected by default
    val selectedIndex: Int = 0
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onFooterSelect(index: Int) = _uiState.update { it.copy(selectedIndex = index) }
}
