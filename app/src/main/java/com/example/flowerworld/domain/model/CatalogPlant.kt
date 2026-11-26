package com.example.flowerworld.domain.model

/** A plant found in an online catalog (not yet one of the user's plants). */
data class CatalogPlant(
    val id: Int,
    val commonName: String?,
    val scientificName: String,
    val family: String?,
    val imageUrl: String?,
) {
    /** The name to show first: the common name when the catalog knows one. */
    val displayName: String get() = commonName ?: scientificName
}

/** Colors the catalog can filter flowers and leaves by. */
enum class PlantColor(val apiValue: String) {
    White("white"),
    Red("red"),
    Brown("brown"),
    Orange("orange"),
    Yellow("yellow"),
    Lime("lime"),
    Green("green"),
    Cyan("cyan"),
    Blue("blue"),
    Purple("purple"),
    Magenta("magenta"),
    Grey("grey"),
    Black("black"),
}

/** Features a search can be narrowed by; a null or false value means "any". */
data class CatalogFilters(
    val flowerColor: PlantColor? = null,
    val foliageColor: PlantColor? = null,
    val edibleOnly: Boolean = false,
) {
    val isEmpty: Boolean get() = flowerColor == null && foliageColor == null && !edibleOnly
}

/** One page of search results; [hasMore] is true when a next page exists. */
data class CatalogPage(val plants: List<CatalogPlant>, val hasMore: Boolean)
