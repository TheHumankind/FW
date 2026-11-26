package com.example.flowerworld.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.HumidityLevel
import com.example.flowerworld.domain.model.LightLevel
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.domain.model.Season
import com.example.flowerworld.domain.model.SpeciesGroup
import com.example.flowerworld.domain.model.SpeciesGrowthRate
import com.example.flowerworld.domain.model.ToxicityLevel
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppDropdown
import com.example.flowerworld.ui.components.AppTextField
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import java.time.LocalDate

/** The two-letter language code of the app (it follows the language chosen in the settings). */
@Composable
fun appLanguageCode(): String = LocalConfiguration.current.locales[0].language

/** The bundled plant guide: search by name in any language, narrow by kind, light and pet safety. */
@Composable
fun GuideSearch(
    onSpeciesClick: (PlantSpecies) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpeciesSearchViewModel = viewModel(factory = SpeciesSearchViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    Column(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.ListPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                label = stringResource(R.string.search_hint),
                imeAction = ImeAction.Search,
                onImeAction = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppDropdown(
                    items = listOf<SpeciesGroup?>(null) + SpeciesGroup.entries,
                    selected = state.filters.group,
                    onSelect = viewModel::onGroupSelect,
                    label = stringResource(R.string.guide_kind),
                    itemLabel = { group -> stringResource(group?.nameRes ?: R.string.search_any) },
                    modifier = Modifier.weight(1f)
                )
                AppDropdown(
                    items = listOf<LightLevel?>(null) + LightLevel.entries,
                    selected = state.filters.light,
                    onSelect = viewModel::onLightSelect,
                    label = stringResource(R.string.guide_light),
                    itemLabel = { light -> stringResource(light?.nameRes ?: R.string.search_any) },
                    modifier = Modifier.weight(1f)
                )
            }
            FilterChip(
                selected = state.filters.petSafeOnly,
                onClick = viewModel::onPetSafeToggle,
                label = { Text(stringResource(R.string.guide_pet_safe)) },
                colors = selectableChipColors()
            )
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (val results = state.results) {
                GuideResults.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                GuideResults.Error -> AppCenteredMessage(text = stringResource(R.string.guide_error))
                is GuideResults.Success -> if (results.species.isEmpty()) {
                    AppCenteredMessage(text = stringResource(R.string.search_empty))
                } else {
                    SpeciesList(results.species, onSpeciesClick)
                }
            }
        }
        Text(
            text = stringResource(R.string.species_attribution),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

@Composable
private fun SpeciesList(species: List<PlantSpecies>, onSpeciesClick: (PlantSpecies) -> Unit) {
    val language = appLanguageCode()
    val season = Season.of(LocalDate.now().month)
    LazyColumn(
        contentPadding = PaddingValues(Dimens.ListPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
        modifier = Modifier.fillMaxSize()
    ) {
        items(species, key = { it.id }) { item ->
            SpeciesCard(item, language, season, onClick = { onSpeciesClick(item) })
        }
    }
}

@Composable
private fun SpeciesCard(species: PlantSpecies, language: String, season: Season, onClick: () -> Unit) {
    val title = species.displayName(language)
    val days = species.wateringDaysFor(season)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Eco,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (title != species.name) {
                Text(
                    text = species.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = listOfNotNull(
                    stringResource(species.light.nameRes),
                    days?.let { pluralStringResource(R.plurals.every_n_days, it, it) }
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeciesCardPreview() {
    FlowerWorldTheme {
        SpeciesCard(
            species = PlantSpecies(
                id = "aglaonema", name = "Aglaonema", names = mapOf("ru" to "Аглаонема"),
                description = "", examples = "", careTips = "", origin = "Tropical Asia",
                wateringDays = mapOf(Season.Spring to 7), light = LightLevel.LowIndirect,
                humidity = HumidityLevel.Medium, temperatureC = 18..27, toxicity = ToxicityLevel.Toxic,
                growthRate = SpeciesGrowthRate.Moderate, group = SpeciesGroup.Foliage, isHouseplant = true
            ),
            language = "ru",
            season = Season.Spring,
            onClick = {}
        )
    }
}
