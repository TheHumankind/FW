package com.example.flowerworld.data.remote

import com.example.flowerworld.domain.model.CatalogFilters
import com.example.flowerworld.domain.model.CatalogPage
import com.example.flowerworld.domain.model.CatalogPlant
import com.example.flowerworld.domain.repository.CatalogNotConfiguredException
import com.example.flowerworld.domain.repository.OnlineCatalogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * The Trefle plant catalog (https://trefle.io, data CC-BY-4.0): about 450 000 plants, searchable by name
 * and filterable by flower and leaf color or edibility. The free token allows 60 requests per minute.
 *
 * The JSON is parsed by hand (`org.json`) to avoid a library for three endpoints; the DTO stays inside this class.
 */
class TreflePlantCatalog(private val token: String) : OnlineCatalogRepository {

    override suspend fun search(query: String, filters: CatalogFilters, page: Int): CatalogPage {
        if (token.isBlank()) throw CatalogNotConfiguredException()
        val trimmed = query.trim()
        val params = buildList {
            add("token" to token)
            add("page" to page.toString())
            if (trimmed.isNotEmpty()) add("q" to trimmed)
            filters.flowerColor?.let { add("filter[flower_color]" to it.apiValue) }
            filters.foliageColor?.let { add("filter[foliage_color]" to it.apiValue) }
            if (filters.edibleOnly) add("filter[edible]" to "true")
        }
        val path = if (trimmed.isEmpty()) "plants" else "plants/search"
        val url = "$BaseUrl/$path?" + params.joinToString("&") { (key, value) -> "${encode(key)}=${encode(value)}" }
        return withContext(Dispatchers.IO) { parse(get(url)) }
    }

    private fun get(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = TimeoutMillis
            connection.readTimeout = TimeoutMillis
            connection.setRequestProperty("Accept", "application/json")
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) throw IOException("Trefle answered HTTP $code")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun parse(body: String): CatalogPage = try {
        val root = JSONObject(body)
        val data = root.getJSONArray("data")
        val plants = (0 until data.length()).map { index ->
            val item = data.getJSONObject(index)
            CatalogPlant(
                id = item.getInt("id"),
                commonName = item.optStringOrNull("common_name"),
                scientificName = item.optString("scientific_name"),
                family = item.optStringOrNull("family"),
                // the catalog sometimes gives plain http links, which Android blocks
                imageUrl = item.optStringOrNull("image_url")?.replaceFirst("http://", "https://"),
            )
        }
        CatalogPage(plants, hasMore = root.optJSONObject("links")?.has("next") == true)
    } catch (e: JSONException) {
        throw IOException("Unexpected answer of Trefle", e)
    }

    private fun JSONObject.optStringOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private fun encode(value: String) = URLEncoder.encode(value, Charsets.UTF_8.name())

    private companion object {
        const val BaseUrl = "https://trefle.io/api/v1"
        const val TimeoutMillis = 15_000
    }
}
