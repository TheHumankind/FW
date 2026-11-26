package com.example.flowerworld.data.catalog

import android.content.Context
import com.example.flowerworld.domain.model.HumidityLevel
import com.example.flowerworld.domain.model.LightLevel
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.domain.model.Season
import com.example.flowerworld.domain.model.SpeciesFilters
import com.example.flowerworld.domain.model.SpeciesGroup
import com.example.flowerworld.domain.model.SpeciesGrowthRate
import com.example.flowerworld.domain.model.ToxicityLevel
import com.example.flowerworld.domain.repository.SpeciesCatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.Normalizer

/**
 * Reads the catalog from `assets/plant_catalog.json` (care data, English) and `assets/plant_names.json`
 * (localized names) once, keeps it in memory and searches it there; about 1000 entries.
 */
class AssetSpeciesCatalog(private val context: Context) : SpeciesCatalogRepository {
    private val loadLock = Mutex()
    private var entries: List<Entry>? = null

    /** A species with the text it is searched by, lower-cased and without accents. */
    private class Entry(val species: PlantSpecies) {
        val searchText: String = normalize(
            (listOf(species.name, species.id.replace('-', ' '), species.examples) + species.names.values)
                .joinToString(" ")
        )
        val nameTexts: List<String> = (listOf(species.name) + species.names.values).map(::normalize)
    }

    override suspend fun search(query: String, filters: SpeciesFilters): List<PlantSpecies> {
        val words = normalize(query).split(' ').filter { it.isNotEmpty() }
        return load()
            .filter { entry ->
                val species = entry.species
                (filters.group == null || species.group == filters.group) &&
                    (filters.light == null || species.light == filters.light) &&
                    (!filters.petSafeOnly || species.toxicity == ToxicityLevel.NonToxic) &&
                    words.all { it in entry.searchText }
            }
            // houseplants first, then names that start with the query, then A-Z
            .sortedWith(
                compareBy<Entry> { !it.species.isHouseplant }
                    .thenBy { entry -> if (words.isNotEmpty() && entry.nameTexts.any { it.startsWith(words.first()) }) 0 else 1 }
                    .thenBy { it.species.name }
            )
            .map { it.species }
    }

    override suspend fun find(id: String): PlantSpecies? = load().firstOrNull { it.species.id == id }?.species

    private suspend fun load(): List<Entry> = loadLock.withLock {
        entries ?: withContext(Dispatchers.IO) { read() }.also { entries = it }
    }

    private fun read(): List<Entry> = try {
        val names = readAsset("plant_names.json")?.let(::JSONObject)
        val array = JSONArray(readAsset("plant_catalog.json") ?: throw IOException("plant_catalog.json is missing"))
        (0 until array.length()).map { Entry(parse(array.getJSONObject(it), names)) }
    } catch (e: JSONException) {
        throw IOException("The plant catalog is damaged", e)
    }

    private fun readAsset(name: String): String? = try {
        context.assets.open(name).bufferedReader().use { it.readText() }
    } catch (e: java.io.FileNotFoundException) {
        null
    }

    private fun parse(json: JSONObject, allNames: JSONObject?): PlantSpecies {
        val id = json.getString("id")
        val category = json.optString("category")
        val localized = allNames?.optJSONObject(id)
        return PlantSpecies(
            id = id,
            name = json.getString("typeName"),
            names = localized?.keys()?.asSequence()?.associateWith { localized.getString(it) }.orEmpty(),
            description = json.optString("description"),
            examples = json.optString("commonExamples"),
            careTips = json.optString("careTips"),
            origin = json.optString("origin"),
            wateringDays = buildMap {
                json.optIntOrNull("springInterval")?.let { put(Season.Spring, it) }
                json.optIntOrNull("summerInterval")?.let { put(Season.Summer, it) }
                json.optIntOrNull("fallInterval")?.let { put(Season.Autumn, it) }
                json.optIntOrNull("winterInterval")?.let { put(Season.Winter, it) }
            },
            light = when (json.optString("lightPreference")) {
                "lowIndirect" -> LightLevel.LowIndirect
                "mediumIndirect" -> LightLevel.MediumIndirect
                "brightIndirect" -> LightLevel.BrightIndirect
                "strongDirect" -> LightLevel.StrongDirect
                "outdoorPartialSun" -> LightLevel.OutdoorPartialSun
                "outdoorShade" -> LightLevel.OutdoorShade
                else -> LightLevel.OutdoorFullSun
            },
            humidity = when (json.optString("humidityPreference")) {
                "low" -> HumidityLevel.Low
                "high" -> HumidityLevel.High
                "veryHigh" -> HumidityLevel.VeryHigh
                else -> HumidityLevel.Medium
            },
            temperatureC = json.optJSONArray("temperaturePreference")
                ?.takeIf { it.length() == 2 }?.let { it.getInt(0)..it.getInt(1) } ?: 15..25,
            toxicity = when (json.optString("plantToxicity")) {
                "nonToxic" -> ToxicityLevel.NonToxic
                "mildlyToxic" -> ToxicityLevel.MildlyToxic
                "toxic" -> ToxicityLevel.Toxic
                else -> ToxicityLevel.Unknown
            },
            growthRate = when (json.optString("growthRate")) {
                "slow" -> SpeciesGrowthRate.Slow
                "fast" -> SpeciesGrowthRate.Fast
                else -> SpeciesGrowthRate.Moderate
            },
            group = groupOf(category),
            isHouseplant = category.startsWith("Houseplants") ||
                category == "Specialty - Carnivorous" || category == "Specialty - Epiphytes & Moss",
        )
    }

    private fun groupOf(category: String): SpeciesGroup = when {
        category == "Houseplants - Succulents" || category == "Houseplants - Cacti" -> SpeciesGroup.Succulents
        category == "Houseplants - Flowering" -> SpeciesGroup.Flowering
        category.startsWith("Houseplants") -> SpeciesGroup.Foliage
        category.startsWith("Vegetables") || category == "Fruits & Berries" || category == "Nuts & Nut Trees" ||
            category == "Herbs" || category == "Sprouts & Microgreens" -> SpeciesGroup.Edible
        category.startsWith("Specialty") -> SpeciesGroup.Special
        else -> SpeciesGroup.Garden
    }

    private fun JSONObject.optIntOrNull(name: String): Int? = if (isNull(name) || !has(name)) null else optInt(name)

    private companion object {
        /** Lower case, accents and diacritics removed (so "ё" matches "е"), for forgiving search. */
        fun normalize(text: String): String =
            Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "").trim()
    }
}
