package com.example.flowerworld.feature.onlinesearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.CatalogFilters
import com.example.flowerworld.domain.model.CatalogPlant
import com.example.flowerworld.domain.model.PlantColor
import com.example.flowerworld.feature.guide.selectableChipColors
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppDropdown
import com.example.flowerworld.ui.components.AppTextField
import com.example.flowerworld.ui.components.PhotoBox
import com.example.flowerworld.ui.labels.nameRes
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** The online catalog (Trefle): by name, flower and leaf color, edibility. Not shown for now, see [GuideViewer]. */
@Composable
internal fun OnlineSearch(
    onPlantClick: (CatalogPlant) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnlineSearchViewModel = viewModel(factory = OnlineSearchViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    SearchContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSearch = {
            // results need the room the keyboard takes
            focusManager.clearFocus()
            keyboard?.hide()
            viewModel.onSearch()
        },
        onFlowerColorSelect = viewModel::onFlowerColorSelect,
        onFoliageColorSelect = viewModel::onFoliageColorSelect,
        onEdibleToggle = viewModel::onEdibleToggle,
        onLoadMore = viewModel::onLoadMore,
        onPlantClick = onPlantClick,
        modifier = modifier
    )
}

@Composable
private fun SearchContent(
    state: OnlineSearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFlowerColorSelect: (PlantColor?) -> Unit,
    onFoliageColorSelect: (PlantColor?) -> Unit,
    onEdibleToggle: () -> Unit,
    onLoadMore: () -> Unit,
    onPlantClick: (CatalogPlant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.ListPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTextField(
                value = state.query,
                onValueChange = onQueryChange,
                label = stringResource(R.string.search_hint),
                imeAction = ImeAction.Search,
                onImeAction = onSearch,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorDropdown(
                    selected = state.filters.flowerColor,
                    onSelect = onFlowerColorSelect,
                    label = stringResource(R.string.search_flower_color),
                    modifier = Modifier.weight(1f)
                )
                ColorDropdown(
                    selected = state.filters.foliageColor,
                    onSelect = onFoliageColorSelect,
                    label = stringResource(R.string.search_foliage_color),
                    modifier = Modifier.weight(1f)
                )
            }
            FilterChip(
                selected = state.filters.edibleOnly,
                onClick = onEdibleToggle,
                label = { Text(stringResource(R.string.search_edible)) },
                colors = selectableChipColors()
            )
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Results(state.results, onPlantClick, onLoadMore)
        }
        Text(
            text = stringResource(R.string.search_attribution),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

@Composable
private fun ColorDropdown(
    selected: PlantColor?,
    onSelect: (PlantColor?) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    AppDropdown(
        items = listOf<PlantColor?>(null) + PlantColor.entries,
        selected = selected,
        onSelect = onSelect,
        label = label,
        itemLabel = { color -> stringResource(color?.nameRes ?: R.string.search_any) },
        modifier = modifier
    )
}

@Composable
private fun Results(results: OnlineSearchResults, onPlantClick: (CatalogPlant) -> Unit, onLoadMore: () -> Unit) {
    when (results) {
        OnlineSearchResults.Idle -> AppCenteredMessage(text = stringResource(R.string.search_idle))
        OnlineSearchResults.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is OnlineSearchResults.Error -> AppCenteredMessage(
            text = stringResource(
                when (results.error) {
                    OnlineSearchError.NotConfigured -> R.string.search_error_not_configured
                    OnlineSearchError.Network -> R.string.search_error_network
                }
            )
        )
        is OnlineSearchResults.Success -> if (results.plants.isEmpty()) {
            AppCenteredMessage(text = stringResource(R.string.search_empty))
        } else {
            ResultList(results, onPlantClick, onLoadMore)
        }
    }
}

@Composable
private fun ResultList(results: OnlineSearchResults.Success, onPlantClick: (CatalogPlant) -> Unit, onLoadMore: () -> Unit) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(Dimens.ListPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
        modifier = Modifier.fillMaxSize()
    ) {
        items(results.plants, key = { it.id }) { plant ->
            ResultCard(plant, onClick = { onPlantClick(plant) })
        }
        if (results.hasMore) {
            item {
                // reaching the end of the list asks for the next page
                LaunchedEffect(results.plants.size) { onLoadMore() }
                Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ResultCard(plant: CatalogPlant, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhotoBox(
            photoUri = plant.imageUrl,
            contentDescription = plant.displayName,
            placeholder = Icons.Filled.Eco,
            placeholderTint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.secondary)
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = plant.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (plant.commonName != null) {
                Text(
                    text = plant.scientificName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            plant.family?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentPreview() {
    FlowerWorldTheme {
        SearchContent(
            state = OnlineSearchUiState(
                query = "monstera",
                filters = CatalogFilters(flowerColor = PlantColor.White),
                results = OnlineSearchResults.Success(
                    plants = listOf(CatalogPlant(1, "Swiss cheese plant", "Monstera deliciosa", "Araceae", null)),
                    hasMore = false
                )
            ),
            onQueryChange = {}, onSearch = {}, onFlowerColorSelect = {}, onFoliageColorSelect = {},
            onEdibleToggle = {}, onLoadMore = {}, onPlantClick = {}
        )
    }
}
