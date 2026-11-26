package com.example.flowerworld.reminder

import com.example.flowerworld.domain.model.Plant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class WateringSummaryTest {
    private val zone = ZoneId.of("UTC")
    private val now = Instant.parse("2026-11-26T09:00:00Z").toEpochMilli()

    /** A plant whose next watering is at [nextWatering]. */
    private fun plantDueAt(nextWatering: String): Plant {
        val intervalHours = 24
        return Plant(
            id = nextWatering,
            title = "Title",
            notes = "",
            name = "Name",
            wateringIntervalHours = intervalHours,
            photos = emptyList(),
            lastWateredAt = Instant.parse(nextWatering).toEpochMilli() - TimeUnit.HOURS.toMillis(intervalHours.toLong()),
        )
    }

    @Test
    fun countsOverdueAndDueTodaySeparately() {
        val plants = listOf(
            plantDueAt("2026-11-25T23:00:00Z"), // yesterday: overdue
            plantDueAt("2026-11-20T10:00:00Z"), // long overdue
            plantDueAt("2026-11-26T05:00:00Z"), // today, earlier than now
            plantDueAt("2026-11-26T23:59:00Z"), // today, later than now
            plantDueAt("2026-11-27T00:00:00Z"), // tomorrow: not counted
        )

        val summary = WateringSummary.of(plants, now, zone)

        assertEquals(WateringSummary(dueToday = 2, overdue = 2), summary)
    }

    @Test
    fun emptyWhenNothingIsDue() {
        val summary = WateringSummary.of(listOf(plantDueAt("2026-12-01T00:00:00Z")), now, zone)

        assertTrue(summary.isEmpty)
    }

    @Test
    fun theDayStartsInTheGivenTimeZone() {
        // 2026-11-26T23:30Z is already the 27th in Warsaw (UTC+1), so it is not "today" there
        val plant = plantDueAt("2026-11-26T23:30:00Z")

        val summary = WateringSummary.of(listOf(plant), now, ZoneId.of("Europe/Warsaw"))

        assertEquals(WateringSummary(dueToday = 0, overdue = 0), summary)
    }
}
