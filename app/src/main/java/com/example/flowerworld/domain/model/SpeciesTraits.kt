package com.example.flowerworld.domain.model

/** How much light a species wants. */
enum class LightLevel { LowIndirect, MediumIndirect, BrightIndirect, StrongDirect, OutdoorFullSun, OutdoorPartialSun, OutdoorShade }

enum class HumidityLevel { Low, Medium, High, VeryHigh }

/** How harmful a species is for pets and people; [Unknown] when the source does not say. */
enum class ToxicityLevel { NonToxic, MildlyToxic, Toxic, Unknown }

enum class SpeciesGrowthRate { Slow, Moderate, Fast }

/** Rough kinds of plants the catalog can be narrowed by. */
enum class SpeciesGroup { Foliage, Succulents, Flowering, Edible, Garden, Special }
