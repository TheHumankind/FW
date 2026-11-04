package com.example.flowerworld.domain.model

import java.time.LocalDate

/**
 * A plant the user takes care of.
 *
 * Domain model: independent of Room entities and future network DTOs.
 *
 * @property id string UUID, generated on the client.
 * @property title species or kind of the plant, e.g. "Monstera deliciosa".
 * @property description free-form notes about the plant.
 * @property name the user's personal name for this plant, e.g. "Bob".
 * @property wateringIntervalHours hours between waterings, set by the user per plant.
 * @property photos photo history: date the photo was taken -> path of the compressed photo in app storage.
 *   One photo per date; empty if there are none.
 */
data class Plant(
    val id: String,
    val title: String,
    val description: String,
    val name: String,
    val wateringIntervalHours: Int,
    val photos: Map<LocalDate, String>,
)
