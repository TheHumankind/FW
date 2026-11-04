package com.example.flowerworld.domain.model

import java.util.concurrent.TimeUnit

/**
 * A plant the user takes care of.
 *
 * Domain model: independent of Room entities and future network DTOs.
 *
 * @property id string UUID, generated on the client.
 * @property title species or kind of the plant, e.g. "Monstera deliciosa".
 * @property notes free-form notes about the plant.
 * @property name the user's personal name for this plant, e.g. "Bob".
 * @property wateringIntervalHours hours between waterings, set by the user per plant.
 * @property photos photo history, in any order (see [photosNewestFirst]); empty if there are none.
 * @property lastWateredAt epoch millis of the last watering; for a new plant, the time it was added.
 * @property diedAt epoch millis when the user marked the plant as dead (it lives in the graveyard then); null while alive.
 */
data class Plant(
    val id: String,
    val title: String,
    val notes: String,
    val name: String,
    val wateringIntervalHours: Int,
    val photos: List<PlantPhoto>,
    val lastWateredAt: Long = 0L,
    val diedAt: Long? = null,
) {
    /** Epoch millis when the plant needs its next watering. */
    val nextWateringAt: Long get() = lastWateredAt + TimeUnit.HOURS.toMillis(wateringIntervalHours.toLong())

    /** Path of the most recent photo, or null if there are none. */
    val latestPhoto: String? get() = photos.maxByOrNull { it.takenAt }?.path

    /** The photos, the most recent first. */
    val photosNewestFirst: List<PlantPhoto> get() = photos.sortedByDescending { it.takenAt }

    /** The text a card shows as the plant's heading: its title (species) or its personal name. */
    fun headingText(heading: PlantHeading): String = when (heading) {
        PlantHeading.Title -> title
        PlantHeading.Name -> name
    }

    /** True when the plant has to be watered already. */
    fun isWateringDue(nowMillis: Long): Boolean = nextWateringAt <= nowMillis

    /**
     * Whole [unit]s left until the next watering; 0 when it is already due.
     * Days are rounded down, so the last day shows "0 days" (water it today); hours are rounded up.
     */
    fun timeUntilNextWatering(nowMillis: Long, unit: IntervalUnit): Int {
        val left = nextWateringAt - nowMillis
        val unitMillis = TimeUnit.HOURS.toMillis(unit.hours.toLong())
        val count = when (unit) {
            IntervalUnit.Days -> Math.floorDiv(left, unitMillis)
            IntervalUnit.Hours -> Math.ceilDiv(left, unitMillis)
        }
        return count.coerceAtLeast(0L).toInt()
    }
}
