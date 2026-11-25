package com.example.flowerworld.reminder

import com.example.flowerworld.domain.model.Plant
import java.time.Instant
import java.time.ZoneId

/**
 * What the daily notification reports.
 *
 * @property dueToday plants whose watering falls on the current day (including the hours that already passed).
 * @property overdue plants whose watering was due before the current day started.
 */
data class WateringSummary(val dueToday: Int, val overdue: Int) {
    val isEmpty: Boolean get() = dueToday == 0 && overdue == 0

    companion object {
        fun of(plants: List<Plant>, nowMillis: Long, zone: ZoneId = ZoneId.systemDefault()): WateringSummary {
            val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
            val startOfToday = today.atStartOfDay(zone).toInstant().toEpochMilli()
            val endOfToday = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val overdue = plants.count { it.nextWateringAt < startOfToday }
            val dueToday = plants.count { it.nextWateringAt in startOfToday until endOfToday }
            return WateringSummary(dueToday, overdue)
        }
    }
}
