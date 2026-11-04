package com.example.flowerworld.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.repository.SettingsRepository
import com.example.flowerworld.ui.components.FooterTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    // Selected footer tab; plants are shown by default
    val selectedTab: FooterTab = FooterTab.Plants,
    val plants: List<Plant> = emptyList(),
    // One-time hint about battery optimization and autostart, shown once the user has plants
    val showReminderHint: Boolean = false
)

class MainViewModel(
    private val plantRepository: PlantRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val selectedTab = MutableStateFlow(FooterTab.Plants)

    val uiState: StateFlow<MainUiState> = combine(
        selectedTab,
        plantRepository.plants,
        settingsRepository.reminderHintShown
    ) { tab, plants, hintShown ->
        MainUiState(
            selectedTab = tab,
            plants = plants,
            showReminderHint = plants.isNotEmpty() && !hintShown
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMillis), MainUiState())

    fun onFooterSelect(tab: FooterTab) = selectedTab.update { tab }

    fun onWaterPlant(plant: Plant) {
        viewModelScope.launch { plantRepository.markWatered(plant.id) }
    }

    fun onDeletePlant(plant: Plant) {
        viewModelScope.launch { plantRepository.delete(plant.id) }
    }

    fun onReminderHintDismiss() {
        viewModelScope.launch { settingsRepository.markReminderHintShown() }
    }

    companion object {
        private const val StopTimeoutMillis = 5_000L

        val Factory = containerViewModelFactory { MainViewModel(it.plantRepository, it.settingsRepository) }
    }
}
