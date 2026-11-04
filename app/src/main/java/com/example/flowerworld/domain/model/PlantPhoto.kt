package com.example.flowerworld.domain.model

/**
 * One photo of a plant.
 *
 * @property id string UUID, generated on the client.
 * @property path path of the compressed photo in app storage.
 * @property takenAt epoch millis when the photo was added.
 */
data class PlantPhoto(
    val id: String,
    val path: String,
    val takenAt: Long,
)
