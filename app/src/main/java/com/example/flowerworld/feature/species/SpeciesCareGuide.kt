package com.example.flowerworld.feature.species

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import java.time.LocalDate

/**
 * The care data of one catalog species as a column of sections: description, watering by season (the
 * current season in bold), conditions and care tips. Shared by the guide screen and the screen of a plant
 * that is linked to a guide entry.
 */
@Composable
fun SpeciesCareGuide(species: PlantSpecies, modifier: Modifier = Modifier) {
    val language = appLanguageCode()
    val currentSeason = Season.of(LocalDate.now().month)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (species.description.isNotBlank()) {
            Text(species.descriptionFor(language), style = MaterialTheme.typography.bodyLarge)
        }
        if (species.examples.isNotBlank()) Text(species.examples, style = MaterialTheme.typography.bodySmall)

        GuideSection(stringResource(R.string.species_watering)) {
            Season.entries.forEach { season ->
                val days = species.wateringDays[season]
                GuideRow(
                    label = stringResource(season.nameRes),
                    value = days?.let { pluralStringResource(R.plurals.every_n_days, it, it) }
                        ?: stringResource(R.string.toxicity_unknown),
                    emphasized = season == currentSeason
                )
            }
        }
        GuideSection(stringResource(R.string.species_conditions)) {
            GuideRow(stringResource(R.string.guide_light), stringResource(species.light.nameRes))
            GuideRow(stringResource(R.string.species_humidity), stringResource(species.humidity.nameRes))
            GuideRow(
                stringResource(R.string.species_temperature),
                stringResource(R.string.species_temperature_range, species.temperatureC.first, species.temperatureC.last)
            )
            GuideRow(stringResource(R.string.species_safety), stringResource(species.toxicity.nameRes))
            GuideRow(stringResource(R.string.species_growth), stringResource(species.growthRate.nameRes))
            if (species.origin.isNotBlank()) GuideRow(stringResource(R.string.species_origin), species.origin)
        }
        if (species.careTips.isNotBlank()) {
            GuideSection(stringResource(R.string.species_care_tips)) {
                Text(species.careTipsFor(language), style = MaterialTheme.typography.bodyMedium)
            }
        }
        val note = if (species.isFullyTranslated(language)) {
            stringResource(R.string.species_attribution)
        } else {
            stringResource(R.string.species_english_only) + "\n" + stringResource(R.string.species_attribution)
        }
        Text(text = note, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun GuideSection(title: String, content: @Composable () -> Unit) {
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
private fun GuideRow(label: String, value: String, emphasized: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasized) FontWeight.Bold else null
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
private fun SpeciesCareGuidePreview() {
    FlowerWorldTheme {
        SpeciesCareGuide(
            PlantSpecies(
                id = "aglaonema", name = "Aglaonema", names = emptyMap(),
                description = "Low-light tolerant aroids with variegated foliage.", examples = "A. commutatum",
                careTips = "Water when the top soil feels dry.", origin = "Tropical Asia",
                wateringDays = mapOf(Season.Spring to 7, Season.Summer to 7, Season.Autumn to 10, Season.Winter to 14),
                light = LightLevel.LowIndirect, humidity = HumidityLevel.Medium, temperatureC = 18..27,
                toxicity = ToxicityLevel.Toxic, growthRate = SpeciesGrowthRate.Moderate,
                group = SpeciesGroup.Foliage, isHouseplant = true
            )
        )
    }
}
