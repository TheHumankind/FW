package com.example.flowerworld.data.fake

import com.example.flowerworld.R
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.model.PlantPhoto
import java.time.LocalDate
import java.time.ZoneOffset

// TEST DATA: made up, not real user content. Remove together with the test photos
// (res/drawable-nodpi/test_plant_*) once the fake repository is replaced by Room.

private const val ResourceUriPrefix = "android.resource://com.example.flowerworld/"

private fun drawableUri(resId: Int): String = ResourceUriPrefix + resId

private fun photo(id: Int, date: LocalDate, resId: Int) = PlantPhoto(
    id = "10000000-0000-4000-8000-%012d".format(id),
    path = drawableUri(resId),
    takenAt = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
)

/** Plants for rendering the UI before real data exists. */
val TestPlants: List<Plant> = listOf(
    Plant(
        id = "00000000-0000-4000-8000-000000000001",
        title = "Seedling",
        notes = "A young sprout that grows near the kitchen window.",
        name = "Sprout",
        wateringIntervalHours = 48,
        photos = listOf(
            photo(1, LocalDate.of(2026, 10, 1), R.drawable.test_plant_1),
            photo(2, LocalDate.of(2026, 10, 20), R.drawable.test_plant_2),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000002",
        title = "Linden",
        notes = "Likes bright light, water when the top soil is dry.",
        name = "Lindy",
        wateringIntervalHours = 72,
        photos = listOf(
            photo(3, LocalDate.of(2026, 10, 15), R.drawable.test_plant_2),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000003",
        title = "Hyacinth",
        notes = "Two bulbs in a wicker pot, waiting for the flowers.",
        name = "Jacinta",
        wateringIntervalHours = 168,
        photos = listOf(
            photo(4, LocalDate.of(2026, 9, 28), R.drawable.test_plant_3),
            photo(5, LocalDate.of(2026, 10, 25), R.drawable.test_plant_3),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000004",
        title = "Unknown plant",
        notes = "",
        name = "Mystery",
        wateringIntervalHours = 96,
        photos = emptyList(),
    ),
)
