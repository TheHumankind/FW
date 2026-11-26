package com.example.flowerworld.feature.search

import androidx.lifecycle.viewModelScope
import com.example.flowerworld.di.containerViewModelFactory
import com.example.flowerworld.domain.model.CatalogFilters
import com.example.flowerworld.domain.model.CatalogPlant
import com.example.flowerworld.domain.model.PlantColor
import com.example.flowerworld.domain.repository.CatalogNotConfiguredException
import com.example.flowerworld.domain.repository.PlantCatalogRepository
import com.example.flowerworld.ui.UiStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

/** Why the last search failed. */
enum class SearchError { NotConfigured, Network }

/** What the search screen shows below the filters. */
sealed interface SearchResults {
    /** Nothing was searched yet. */
    data object Idle : SearchResults
    data object Loading : SearchResults
    data class Success(
        val plants: List<CatalogPlant>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : SearchResults
    data class Error(val error: SearchError) : SearchResults
}

data class SearchUiState(
    val query: String = "",
    val filters: CatalogFilters = CatalogFilters(),
    val results: SearchResults = SearchResults.Idle,
)

class SearchViewModel(
    private val catalog: PlantCatalogRepository
) : UiStateViewModel<SearchUiState>(SearchUiState()) {
    private var searchJob: Job? = null
    private var nextPage = 1

    fun onQueryChange(value: String) = updateState { copy(query = value) }

    fun onFlowerColorSelect(color: PlantColor?) = changeFilters { copy(flowerColor = color) }

    fun onFoliageColorSelect(color: PlantColor?) = changeFilters { copy(foliageColor = color) }

    fun onEdibleToggle() = changeFilters { copy(edibleOnly = !edibleOnly) }

    /** Starts a new search from the first page with the current query and filters. */
    fun onSearch() {
        searchJob?.cancel()
        val state = currentState
        if (state.query.isBlank() && state.filters.isEmpty) {
            updateState { copy(results = SearchResults.Idle) }
            return
        }
        nextPage = 1
        updateState { copy(results = SearchResults.Loading) }
        searchJob = viewModelScope.launch {
            val results = try {
                val page = catalog.search(state.query, state.filters, nextPage)
                nextPage++
                SearchResults.Success(page.plants, page.hasMore)
            } catch (e: CatalogNotConfiguredException) {
                SearchResults.Error(SearchError.NotConfigured)
            } catch (e: IOException) {
                SearchResults.Error(SearchError.Network)
            }
            updateState { copy(results = results) }
        }
    }

    /** Appends the next page when the user scrolled to the end of the list. */
    fun onLoadMore() {
        val state = currentState
        val results = state.results as? SearchResults.Success ?: return
        if (!results.hasMore || results.isLoadingMore) return
        updateState { copy(results = results.copy(isLoadingMore = true)) }
        searchJob = viewModelScope.launch {
            try {
                val page = catalog.search(state.query, state.filters, nextPage)
                nextPage++
                updateState { copy(results = SearchResults.Success(results.plants + page.plants, page.hasMore)) }
            } catch (e: IOException) {
                // keep what is shown; the next scroll to the end tries again
                updateState { copy(results = results.copy(isLoadingMore = false)) }
            }
        }
    }

    private fun changeFilters(transform: CatalogFilters.() -> CatalogFilters) {
        updateState { copy(filters = filters.transform()) }
        onSearch()
    }

    companion object {
        val Factory = containerViewModelFactory { SearchViewModel(it.plantCatalog) }
    }
}
