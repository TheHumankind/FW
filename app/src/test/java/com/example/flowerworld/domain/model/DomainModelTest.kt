package com.example.flowerworld.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Month
import java.util.concurrent.TimeUnit

class DomainModelTest {
    @Test
    fun parseIntervalHoursConvertsDaysAndHours() {
        assertEquals(72, parseIntervalHours("3", IntervalUnit.Days))
        assertEquals(12, parseIntervalHours(" 12 ", IntervalUnit.Hours))
    }

    @Test
    fun parseIntervalHoursRejectsBadInput() {
        assertNull(parseIntervalHours("", IntervalUnit.Days))
        assertNull(parseIntervalHours("0", IntervalUnit.Days))
        assertNull(parseIntervalHours("-2", IntervalUnit.Hours))
        assertNull(parseIntervalHours("two", IntervalUnit.Days))
        assertNull(parseIntervalHours("999999999999", IntervalUnit.Days))
    }

    @Test
    fun seasonsFollowTheNorthernHemisphere() {
        assertEquals(Season.Winter, Season.of(Month.DECEMBER))
        assertEquals(Season.Winter, Season.of(Month.FEBRUARY))
        assertEquals(Season.Spring, Season.of(Month.MARCH))
        assertEquals(Season.Summer, Season.of(Month.AUGUST))
        assertEquals(Season.Autumn, Season.of(Month.NOVEMBER))
    }

    @Test
    fun timeUntilNextWateringRoundsDaysDownAndHoursUp() {
        val plant = Plant(
            id = "1", title = "t", notes = "", name = "n", wateringIntervalHours = 60, photos = emptyList(),
            lastWateredAt = 0L,
        )
        val now = TimeUnit.HOURS.toMillis(10) // 50 hours left

        assertEquals(2, plant.timeUntilNextWatering(now, IntervalUnit.Days)) // 2.08 days
        assertEquals(50, plant.timeUntilNextWatering(now, IntervalUnit.Hours))
        // 50.5 hours left: hours are rounded up
        assertEquals(51, plant.timeUntilNextWatering(now - TimeUnit.MINUTES.toMillis(30), IntervalUnit.Hours))
    }

    @Test
    fun overduePlantHasZeroTimeLeft() {
        val plant = Plant(
            id = "1", title = "t", notes = "", name = "n", wateringIntervalHours = 24, photos = emptyList(),
            lastWateredAt = 0L,
        )
        val now = TimeUnit.DAYS.toMillis(5)

        assertTrue(plant.isWateringDue(now))
        assertEquals(0, plant.timeUntilNextWatering(now, IntervalUnit.Days))
    }

    @Test
    fun speciesWateringFallsBackToTheNearestKnownSeason() {
        val species = species(wateringDays = mapOf(Season.Summer to 7, Season.Autumn to 10))

        assertEquals(10, species.wateringDaysFor(Season.Autumn))
        assertEquals(7, species.wateringDaysFor(Season.Winter)) // no data for winter: summer is the nearest known
        assertNull(species(wateringDays = emptyMap()).wateringDaysFor(Season.Spring))
    }

    @Test
    fun speciesShowsTheLocalizedNameWhenThereIsOne() {
        val species = species(names = mapOf("ru" to "Аглаонема"))

        assertEquals("Аглаонема", species.displayName("ru"))
        assertEquals("Aglaonema", species.displayName("pl"))
    }

    private fun species(
        wateringDays: Map<Season, Int> = emptyMap(),
        names: Map<String, String> = emptyMap(),
    ) = PlantSpecies(
        id = "aglaonema",
        name = "Aglaonema",
        names = names,
        description = "",
        examples = "",
        careTips = "",
        origin = "",
        wateringDays = wateringDays,
        light = LightLevel.LowIndirect,
        humidity = HumidityLevel.Medium,
        temperatureC = 18..27,
        toxicity = ToxicityLevel.Toxic,
        growthRate = SpeciesGrowthRate.Moderate,
        group = SpeciesGroup.Foliage,
        isHouseplant = true,
    )
}
