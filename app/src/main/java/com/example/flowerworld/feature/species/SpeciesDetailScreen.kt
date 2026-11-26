package com.example.flowerworld.feature.species

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.flowerworld.feature.search.appLanguageCode
import com.example.flowerworld.feature.search.nameRes
import com.example.flowerworld.ui.components.AppButton
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppScreen
import com.example.flowerworld.ui.components.AppTopBar
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import java.time.LocalDate

/**
 * The care guide of one catalog species. "Add to my plants" calls [onAddClick] with the name to show and the
 * watering interval in days for the current season (null when the catalog has none).
 */
@Composable
fun SpeciesDetailScreen(
    onBackClick: () -> Unit,
    onAddClick: (title: String, intervalDays: Int?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpeciesDetailViewModel = viewModel(factory = SpeciesDetailViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val language = appLanguageCode()
    AppScreen(modifier = modifier.navigationBarsPadding()) {
        when (val current = state) {
            SpeciesDetailUiState.Loading -> AppTopBar(title = "", onBackClick = onBackClick)
            SpeciesDetailUiState.NotFound -> {
                AppTopBar(title = "", onBackClick = onBackClick)
                AppCenteredMessage(text = stringResource(R.string.species_not_found))
            }
            is SpeciesDetailUiState.Success -> SpeciesDetailContent(
                species = current.species,
                language = language,
                onBackClick = onBackClick,
                onAddClick = {
                    val days = current.species.wateringDaysFor(Season.of(LocalDate.now().month))
                    onAddClick(current.species.displayName(language), days)
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.SpeciesDetailContent(
    species: PlantSpecies,
    language: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val title = species.displayName(language)
    AppTopBar(title = title, onBackClick = onBackClick)
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ListPadding, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (title != species.name) {
            Text(text = species.name, style = MaterialTheme.typography.titleMedium)
        }
        if (species.description.isNotBlank()) Text(species.description, style = MaterialTheme.typography.bodyLarge)
        if (species.examples.isNotBlank()) Text(species.examples, style = MaterialTheme.typography.bodySmall)

        Section(stringResource(R.string.species_watering)) {
            Season.entries.forEach { season ->
                val days = species.wateringDays[season]
                InfoRow(
                    label = stringResource(season.nameRes),
                    value = days?.let { pluralStringResource(R.plurals.every_n_days, it, it) }
                        ?: stringResource(R.string.toxicity_unknown)
                )
            }
        }
        Section(stringResource(R.string.species_conditions)) {
            InfoRow(stringResource(R.string.guide_light), stringResource(species.light.nameRes))
            InfoRow(stringResource(R.string.species_humidity), stringResource(species.humidity.nameRes))
            InfoRow(
                stringResource(R.string.species_temperature),
                stringResource(R.string.species_temperature_range, species.temperatureC.first, species.temperatureC.last)
            )
            InfoRow(stringResource(R.string.species_safety), stringResource(species.toxicity.nameRes))
            InfoRow(stringResource(R.string.species_growth), stringResource(species.growthRate.nameRes))
            if (species.origin.isNotBlank()) InfoRow(stringResource(R.string.species_origin), species.origin)
        }
        if (species.careTips.isNotBlank()) {
            Section(stringResource(R.string.species_care_tips)) {
                Text(species.careTips, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            text = stringResource(R.string.species_english_only) + "\n" + stringResource(R.string.species_attribution),
            style = MaterialTheme.typography.bodySmall
        )
    }
    AppButton(
        text = stringResource(R.string.species_add),
        onClick = onAddClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = 16.dp)
    )
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeciesDetailPreview() {
    FlowerWorldTheme {
        AppScreen {
            SpeciesDetailContent(
                species = PlantSpecies(
                    id = "aglaonema", name = "Aglaonema", names = mapOf("ru" to "Аглаонема"),
                    description = "Low-light tolerant aroids with variegated foliage.", examples = "A. commutatum",
                    careTips = "Water when the top soil feels dry.", origin = "Tropical Asia",
                    wateringDays = mapOf(Season.Spring to 7, Season.Summer to 7, Season.Autumn to 10, Season.Winter to 14),
                    light = LightLevel.LowIndirect, humidity = HumidityLevel.Medium, temperatureC = 18..27,
                    toxicity = ToxicityLevel.Toxic, growthRate = SpeciesGrowthRate.Moderate,
                    group = SpeciesGroup.Foliage, isHouseplant = true
                ),
                language = "ru", onBackClick = {}, onAddClick = {}
            )
        }
    }
}
