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
 * @property notes free-form notes about the plant.
 * @property name the user's personal name for this plant, e.g. "Bob".
 * @property wateringIntervalHours hours between waterings, set by the user per plant.
 * @property photos photo history: date the photo was taken -> path of the compressed photo in app storage.
 *   One photo per date; empty if there are none.
 * @property lastWateredAt epoch millis of the last watering; for a new plant, the time it was added.
 * @property diedAt epoch millis when the user marked the plant as dead (it lives in the graveyard then); null while alive.
 */
data class Plant(
    val id: String,
    val title: String,
    val notes: String,
    val name: String,
    val wateringIntervalHours: Int,
    val photos: Map<LocalDate, String>,
    val lastWateredAt: Long = 0L,
    val diedAt: Long? = null,
) {
    /** Epoch millis when the plant needs its next watering. */
    val nextWateringAt: Long get() = lastWateredAt + TimeUnit.HOURS.toMillis(wateringIntervalHours.toLong())

    /** Path of the most recent photo, or null if there are none. */
    val latestPhoto: String? get() = photos.maxByOrNull { it.key }?.value

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
