package com.example.flowerworld.feature.main

import androidx.lifecycle.ViewModel
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.FooterTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    // Selected footer tab; plants are shown by default
    val selectedTab: FooterTab = FooterTab.Plants,
    // TODO: take plants from a repository instead of the test data
    val plants: List<Plant> = TestPlants
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onFooterSelect(tab: FooterTab) = _uiState.update { it.copy(selectedTab = tab) }
}
