package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.CatalogFilters
import com.example.flowerworld.domain.model.CatalogPage
import java.io.IOException

/** The catalog has no access credentials configured, so no request was made. */
class CatalogNotConfiguredException : IllegalStateException("The plant catalog token is not set")

/** Searches an online catalog of plants. */
interface PlantCatalogRepository {
    /**
     * Finds plants by [query] (may be blank when [filters] are set) and [filters]; [page] starts at 1.
     *
     * @throws CatalogNotConfiguredException when the catalog is not set up.
     * @throws IOException when the catalog cannot be reached or answers with an error.
     */
    suspend fun search(query: String, filters: CatalogFilters, page: Int): CatalogPage
}
