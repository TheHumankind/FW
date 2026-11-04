package com.example.flowerworld.feature.plantinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flowerworld.di.appContainer
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.model.parseIntervalHours
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.ui.FieldError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The details of a plant the user can edit; the other rows of the screen are system data. */
enum class PlantField { Name, Title, Notes, WateringInterval }

/** An edit dialog that is open: which [field] it edits and why the last "Save" was refused, if it was. */
data class PlantEdit(val field: PlantField, val error: FieldError? = null)

/** What the plant info screen shows. */
sealed interface PlantInfoUiState {
    data object Loading : PlantInfoUiState
    data class Success(val plant: Plant, val edit: PlantEdit? = null) : PlantInfoUiState

    /** There is no (non-deleted) plant with the requested id. */
    data object Error : PlantInfoUiState
}

/** Observes one plant, chosen by the [PlantIdArg] navigation argument, and edits its details. */
class PlantInfoViewModel(
    private val plantRepository: PlantRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val plantId: String = checkNotNull(savedStateHandle[PlantIdArg])
    private val edit = MutableStateFlow<PlantEdit?>(null)

    val uiState: StateFlow<PlantInfoUiState> = combine(plantRepository.plant(plantId), edit) { plant, edit ->
        if (plant != null) PlantInfoUiState.Success(plant, edit) else PlantInfoUiState.Error
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMillis), PlantInfoUiState.Loading)

    fun onEditRequest(field: PlantField) {
        edit.value = PlantEdit(field)
    }

    /** The user typed or chose something: the error shown for the previous try no longer applies. */
    fun onEditInput() {
        edit.value = edit.value?.copy(error = null)
    }

    fun onEditDismiss() {
        edit.value = null
    }

    /** Saves the open edit: [text] is the new value; [unit] is what a watering interval in [text] is counted in. */
    fun onEditSave(text: String, unit: IntervalUnit) {
        val field = edit.value?.field ?: return
        val plant = (uiState.value as? PlantInfoUiState.Success)?.plant ?: return
        val value = text.trim()
        var title = plant.title
        var name = plant.name
        var notes = plant.notes
        var hours = plant.wateringIntervalHours
        val error: FieldError? = when (field) {
            PlantField.Name -> FieldError.Required.takeIf { value.isEmpty() }.also { name = value }
            PlantField.Title -> FieldError.Required.takeIf { value.isEmpty() }.also { title = value }
            PlantField.Notes -> null.also { notes = value }
            PlantField.WateringInterval -> {
                val parsed = parseIntervalHours(value, unit)
                when {
                    value.isEmpty() -> FieldError.Required
                    parsed == null -> FieldError.InvalidNumber
                    else -> null.also { hours = parsed }
                }
            }
        }
        if (error != null) {
            edit.value = PlantEdit(field, error)
            return
        }
        viewModelScope.launch {
            plantRepository.updateDetails(plant.id, title, name, notes, hours)
            edit.value = null
        }
    }

    companion object {
        const val PlantIdArg = "plantId"
        private const val StopTimeoutMillis = 5_000L

        val Factory = viewModelFactory {
            initializer { PlantInfoViewModel(appContainer.plantRepository, createSavedStateHandle()) }
        }
    }
}
