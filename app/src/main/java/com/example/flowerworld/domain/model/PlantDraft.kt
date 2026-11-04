package com.example.flowerworld.domain.model

/**
 * Validated user input for a plant that does not exist yet.
 *
 * @property photoSourceUri URI of the picked photo that is not in app storage yet, or null.
 */
data class PlantDraft(
    val title: String,
    val notes: String,
    val name: String,
    val wateringIntervalHours: Int,
    val photoSourceUri: String?,
)
