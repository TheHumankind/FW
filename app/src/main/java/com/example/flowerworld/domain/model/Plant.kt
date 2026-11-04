package com.example.flowerworld.domain.model

import java.time.LocalDate
import java.util.concurrent.TimeUnit

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
 * @property lastWateredAt epoch millis of the last watering; for a new plant, the time it was added.
 */
data class Plant(
    val id: String,
    val title: String,
    val description: String,
    val name: String,
    val wateringIntervalHours: Int,
    val photos: Map<LocalDate, String>,
    val lastWateredAt: Long = 0L,
) {
    /** Epoch millis when the plant needs its next watering. */
    val nextWateringAt: Long get() = lastWateredAt + TimeUnit.HOURS.toMillis(wateringIntervalHours.toLong())

    /** Path of the most recent photo, or null if there are none. */
    val latestPhoto: String? get() = photos.maxByOrNull { it.key }?.value

    /** Whole hours left until the next watering, rounded up; 0 when it is already due. */
    fun hoursUntilNextWatering(nowMillis: Long): Int =
        Math.ceilDiv(nextWateringAt - nowMillis, TimeUnit.HOURS.toMillis(1)).coerceAtLeast(0L).toInt()
}
