package com.example.flowerworld.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** What the settings screen shows. */
sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val language: AppLanguage) : SettingsUiState
}

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = settingsRepository.language
        .map { SettingsUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMillis), SettingsUiState.Loading)

    fun onLanguageSelect(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    companion object {
        private const val StopTimeoutMillis = 5_000L

        val Factory = containerViewModelFactory { SettingsViewModel(it.settingsRepository) }
    }
}
