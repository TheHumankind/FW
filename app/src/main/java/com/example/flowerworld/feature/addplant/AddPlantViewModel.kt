package com.example.flowerworld.feature.addplant

import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.PlantDraft
import com.example.flowerworld.domain.usecase.AddPlantUseCase
import com.example.flowerworld.ui.UiStateViewModel
import kotlinx.coroutines.launch
import java.io.IOException

/** Why a form field is not valid; the screen turns it into a message. */
enum class FieldError { Required, InvalidNumber }


data class AddPlantUiState(
    val title: String = "",
    val notes: String = "",
    val name: String = "",
    // Kept as text while typing; parsed on "Add"
    val wateringInterval: String = "",
    val intervalUnit: IntervalUnit = IntervalUnit.Days,
    val photoUri: String? = null,
    val titleError: FieldError? = null,
    val nameError: FieldError? = null,
    val intervalError: FieldError? = null,
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,
    val isSaved: Boolean = false
)

class AddPlantViewModel(
    private val addPlant: AddPlantUseCase
) : UiStateViewModel<AddPlantUiState>(AddPlantUiState()) {
    fun onTitleChange(value: String) = updateState { copy(title = value, titleError = null) }

    fun onNotesChange(value: String) = updateState { copy(notes = value) }

    fun onNameChange(value: String) = updateState { copy(name = value, nameError = null) }

    fun onWateringIntervalChange(value: String) =
        updateState { copy(wateringInterval = value, intervalError = null) }

    fun onIntervalUnitChange(unit: IntervalUnit) = updateState { copy(intervalUnit = unit, intervalError = null) }

    fun onPhotoPicked(uri: String) = updateState { copy(photoUri = uri, saveFailed = false) }

    fun onAddClick() {
        val state = currentState
        if (state.isSaving) return

        val title = state.title.trim()
        val name = state.name.trim()
        val titleError = FieldError.Required.takeIf { title.isEmpty() }
        val nameError = FieldError.Required.takeIf { name.isEmpty() }
        // the interval is stored in hours; a value too big for that counts as invalid
        val interval = state.wateringInterval.trim().toLongOrNull()
            ?.takeIf { it > 0 }
            ?.times(state.intervalUnit.hours)
            ?.takeIf { it <= Int.MAX_VALUE }
            ?.toInt()
        val intervalError = when {
            state.wateringInterval.isBlank() -> FieldError.Required
            interval == null -> FieldError.InvalidNumber
            else -> null
        }
        if (titleError != null || nameError != null || intervalError != null || interval == null) {
            updateState { copy(titleError = titleError, nameError = nameError, intervalError = intervalError) }
            return
        }

        val draft = PlantDraft(
            title = title,
            notes = state.notes.trim(),
            name = name,
            wateringIntervalHours = interval,
            photoSourceUri = state.photoUri
        )
        viewModelScope.launch {
            updateState { copy(isSaving = true, saveFailed = false) }
            try {
                addPlant(draft)
                updateState { copy(isSaving = false, isSaved = true) }
            } catch (e: IOException) {
                updateState { copy(isSaving = false, saveFailed = true) }
            }
        }
    }

    companion object {
        val Factory = containerViewModelFactory { AddPlantViewModel(it.addPlantUseCase) }
    }
}
