package com.example.flowerworld.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.ui.components.FooterTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class MainUiState(
    // Selected footer tab; plants are shown by default
    val selectedTab: FooterTab = FooterTab.Plants,
    val plants: List<Plant> = emptyList()
)

class MainViewModel(plantRepository: PlantRepository) : ViewModel() {
    private val selectedTab = MutableStateFlow(FooterTab.Plants)

    val uiState: StateFlow<MainUiState> = combine(selectedTab, plantRepository.plants) { tab, plants ->
        MainUiState(selectedTab = tab, plants = plants)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMillis), MainUiState())

    fun onFooterSelect(tab: FooterTab) = selectedTab.update { tab }

    companion object {
        private const val StopTimeoutMillis = 5_000L

        val Factory = containerViewModelFactory { MainViewModel(it.plantRepository) }
    }
}
