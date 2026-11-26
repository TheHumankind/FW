package com.example.flowerworld.feature.search

import androidx.annotation.StringRes
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.HumidityLevel
import com.example.flowerworld.domain.model.LightLevel
import com.example.flowerworld.domain.model.Season
import com.example.flowerworld.domain.model.SpeciesGroup
import com.example.flowerworld.domain.model.SpeciesGrowthRate
import com.example.flowerworld.domain.model.ToxicityLevel

@get:StringRes
val SpeciesGroup.nameRes: Int
    get() = when (this) {
        SpeciesGroup.Foliage -> R.string.group_foliage
        SpeciesGroup.Succulents -> R.string.group_succulents
        SpeciesGroup.Flowering -> R.string.group_flowering
        SpeciesGroup.Edible -> R.string.group_edible
        SpeciesGroup.Garden -> R.string.group_garden
        SpeciesGroup.Special -> R.string.group_special
    }

@get:StringRes
val LightLevel.nameRes: Int
    get() = when (this) {
        LightLevel.LowIndirect -> R.string.light_low_indirect
        LightLevel.MediumIndirect -> R.string.light_medium_indirect
        LightLevel.BrightIndirect -> R.string.light_bright_indirect
        LightLevel.StrongDirect -> R.string.light_strong_direct
        LightLevel.OutdoorFullSun -> R.string.light_outdoor_full_sun
        LightLevel.OutdoorPartialSun -> R.string.light_outdoor_partial_sun
        LightLevel.OutdoorShade -> R.string.light_outdoor_shade
    }

@get:StringRes
val HumidityLevel.nameRes: Int
    get() = when (this) {
        HumidityLevel.Low -> R.string.humidity_low
        HumidityLevel.Medium -> R.string.humidity_medium
        HumidityLevel.High -> R.string.humidity_high
        HumidityLevel.VeryHigh -> R.string.humidity_very_high
    }

@get:StringRes
val ToxicityLevel.nameRes: Int
    get() = when (this) {
        ToxicityLevel.NonToxic -> R.string.toxicity_non_toxic
        ToxicityLevel.MildlyToxic -> R.string.toxicity_mild
        ToxicityLevel.Toxic -> R.string.toxicity_toxic
        ToxicityLevel.Unknown -> R.string.toxicity_unknown
    }

@get:StringRes
val SpeciesGrowthRate.nameRes: Int
    get() = when (this) {
        SpeciesGrowthRate.Slow -> R.string.growth_slow
        SpeciesGrowthRate.Moderate -> R.string.growth_moderate
        SpeciesGrowthRate.Fast -> R.string.growth_fast
    }

@get:StringRes
val Season.nameRes: Int
    get() = when (this) {
        Season.Spring -> R.string.season_spring
        Season.Summer -> R.string.season_summer
        Season.Autumn -> R.string.season_autumn
        Season.Winter -> R.string.season_winter
    }
