package com.example.flowerworld.domain.model

import java.time.Month

/** How much light a species wants. */
enum class LightLevel { LowIndirect, MediumIndirect, BrightIndirect, StrongDirect, OutdoorFullSun, OutdoorPartialSun, OutdoorShade }

enum class HumidityLevel { Low, Medium, High, VeryHigh }

/** How harmful a species is for pets and people; [Unknown] when the source does not say. */
enum class ToxicityLevel { NonToxic, MildlyToxic, Toxic, Unknown }

enum class SpeciesGrowthRate { Slow, Moderate, Fast }

/** Rough kinds of plants the catalog can be narrowed by. */
enum class SpeciesGroup { Foliage, Succulents, Flowering, Edible, Garden, Special }

/** Seasons of the northern hemisphere, which the care data is written for. */
enum class Season {
    Spring, Summer, Autumn, Winter;

    companion object {
        fun of(month: Month): Season = when (month) {
            Month.MARCH, Month.APRIL, Month.MAY -> Spring
            Month.JUNE, Month.JULY, Month.AUGUST -> Summer
            Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> Autumn
            else -> Winter
        }
    }
}

/**
 * A kind of plant from the bundled catalog, with its care data (not a plant the user owns).
 *
 * Descriptions and care texts are in English; [names] holds the localized names by language code
 * (`ru`, `uk`, `pl`, `be`), possibly missing.
 *
 * @property wateringDays days between waterings per season; a season without an entry means "no data / dormant".
 */
data class PlantSpecies(
    val id: String,
    val name: String,
    val names: Map<String, String>,
    val description: String,
    val examples: String,
    val careTips: String,
    val origin: String,
    val wateringDays: Map<Season, Int>,
    val light: LightLevel,
    val humidity: HumidityLevel,
    val temperatureC: IntRange,
    val toxicity: ToxicityLevel,
    val growthRate: SpeciesGrowthRate,
    val group: SpeciesGroup,
    val isHouseplant: Boolean,
) {
    /** The name in [languageCode] when the catalog has one, otherwise the English name. */
    fun displayName(languageCode: String): String = names[languageCode] ?: name

    /** Days between waterings now: this [season]'s value, or the nearest known one when the season has none. */
    fun wateringDaysFor(season: Season): Int? =
        wateringDays[season] ?: listOf(Season.Summer, Season.Spring, Season.Autumn, Season.Winter)
            .firstNotNullOfOrNull { wateringDays[it] }
}

/** Narrows a catalog search; null / false means "any". */
data class SpeciesFilters(
    val group: SpeciesGroup? = null,
    val light: LightLevel? = null,
    val petSafeOnly: Boolean = false,
)
