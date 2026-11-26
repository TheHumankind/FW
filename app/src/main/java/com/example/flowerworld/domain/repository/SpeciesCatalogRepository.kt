package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.domain.model.SpeciesFilters
import java.io.IOException

/** The bundled, read-only catalog of common plants with care data; works offline. */
interface SpeciesCatalogRepository {
    /**
     * Species matching [query] (any language, may be blank) and [filters], houseplants first.
     *
     * @throws IOException when the bundled data cannot be read.
     */
    suspend fun search(query: String, filters: SpeciesFilters): List<PlantSpecies>

    /** The species with [id], or null if there is none. */
    suspend fun find(id: String): PlantSpecies?
}
