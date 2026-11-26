package com.example.flowerworld.feature.guide

import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.LightLevel
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.domain.model.SpeciesFilters
import com.example.flowerworld.domain.model.SpeciesGroup
import com.example.flowerworld.domain.repository.SpeciesCatalogRepository
import com.example.flowerworld.ui.UiStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

/** What the plant guide shows below the filters. */
sealed interface GuideResults {
    data object Loading : GuideResults
    data class Success(val species: List<PlantSpecies>) : GuideResults
    data object Error : GuideResults
}

data class GuideUiState(
    val query: String = "",
    val filters: SpeciesFilters = SpeciesFilters(),
    val results: GuideResults = GuideResults.Loading,
)

/** Searches the bundled plant catalog; the list is shown right away and narrows as the user types. */
class SpeciesSearchViewModel(
    private val catalog: SpeciesCatalogRepository
) : UiStateViewModel<GuideUiState>(GuideUiState()) {
    private var searchJob: Job? = null

    init {
        refresh()
    }

    fun onQueryChange(value: String) {
        updateState { copy(query = value) }
        refresh()
    }

    fun onGroupSelect(group: SpeciesGroup?) {
        updateState { copy(filters = filters.copy(group = group)) }
        refresh()
    }

    fun onLightSelect(light: LightLevel?) {
        updateState { copy(filters = filters.copy(light = light)) }
        refresh()
    }

    fun onPetSafeToggle() {
        updateState { copy(filters = filters.copy(petSafeOnly = !filters.petSafeOnly)) }
        refresh()
    }

    private fun refresh() {
        searchJob?.cancel()
        val state = currentState
        searchJob = viewModelScope.launch {
            val results = try {
                GuideResults.Success(catalog.search(state.query, state.filters))
            } catch (e: IOException) {
                GuideResults.Error
            }
            updateState { copy(results = results) }
        }
    }

    companion object {
        val Factory = containerViewModelFactory { SpeciesSearchViewModel(it.speciesCatalog) }
    }
}
