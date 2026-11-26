package com.example.flowerworld.feature.species

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flowerworld.di.appContainer
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.domain.repository.SpeciesCatalogRepository
import com.example.flowerworld.ui.UiStateViewModel
import kotlinx.coroutines.launch
import java.io.IOException

/** What the species screen shows. */
sealed interface SpeciesDetailUiState {
    data object Loading : SpeciesDetailUiState
    data class Success(val species: PlantSpecies) : SpeciesDetailUiState
    data object NotFound : SpeciesDetailUiState
}

/** Loads one catalog species, chosen by the [SpeciesIdArg] navigation argument. */
class SpeciesDetailViewModel(
    catalog: SpeciesCatalogRepository,
    savedStateHandle: SavedStateHandle
) : UiStateViewModel<SpeciesDetailUiState>(SpeciesDetailUiState.Loading) {
    init {
        val id: String = checkNotNull(savedStateHandle[SpeciesIdArg])
        viewModelScope.launch {
            val species = try {
                catalog.find(id)
            } catch (e: IOException) {
                null
            }
            updateState { if (species != null) SpeciesDetailUiState.Success(species) else SpeciesDetailUiState.NotFound }
        }
    }

    companion object {
        const val SpeciesIdArg = "speciesId"

        val Factory = viewModelFactory {
            initializer { SpeciesDetailViewModel(appContainer.speciesCatalog, createSavedStateHandle()) }
        }
    }
}
