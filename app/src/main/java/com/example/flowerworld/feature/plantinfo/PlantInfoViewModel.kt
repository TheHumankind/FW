package com.example.flowerworld.feature.plantinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flowerworld.di.appContainer
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** What the plant info screen shows. */
sealed interface PlantInfoUiState {
    data object Loading : PlantInfoUiState
    data class Success(val plant: Plant) : PlantInfoUiState

    /** There is no (non-deleted) plant with the requested id. */
    data object Error : PlantInfoUiState
}

/** Observes one plant, chosen by the [PlantIdArg] navigation argument. */
class PlantInfoViewModel(
    plantRepository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val plantId: String = checkNotNull(savedStateHandle[PlantIdArg])

    val uiState: StateFlow<PlantInfoUiState> = plantRepository.plants
        .map { plants ->
            plants.firstOrNull { it.id == plantId }
                ?.let { PlantInfoUiState.Success(it) }
                ?: PlantInfoUiState.Error
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMillis), PlantInfoUiState.Loading)

    companion object {
        const val PlantIdArg = "plantId"
        private const val StopTimeoutMillis = 5_000L

        val Factory = viewModelFactory {
            initializer { PlantInfoViewModel(appContainer.plantRepository, createSavedStateHandle()) }
        }
    }
}
