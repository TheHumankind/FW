package com.example.flowerworld.data.fake

import com.example.flowerworld.R
import com.example.flowerworld.domain.model.Plant
import java.time.LocalDate

// TEST DATA: made up, not real user content. Remove together with the test photos
// (res/drawable-nodpi/test_plant_*) once the fake repository is replaced by Room.

private const val ResourceUriPrefix = "android.resource://com.example.flowerworld/"

private fun drawableUri(resId: Int): String = ResourceUriPrefix + resId

/** Plants for rendering the UI before real data exists. */
val TestPlants: List<Plant> = listOf(
    Plant(
        id = "00000000-0000-4000-8000-000000000001",
        title = "Seedling",
        notes = "A young sprout that grows near the kitchen window.",
        name = "Sprout",
        wateringIntervalHours = 48,
        photos = mapOf(
            LocalDate.of(2026, 10, 1) to drawableUri(R.drawable.test_plant_1),
            LocalDate.of(2026, 10, 20) to drawableUri(R.drawable.test_plant_2),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000002",
        title = "Linden",
        notes = "Likes bright light, water when the top soil is dry.",
        name = "Lindy",
        wateringIntervalHours = 72,
        photos = mapOf(
            LocalDate.of(2026, 10, 15) to drawableUri(R.drawable.test_plant_2),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000003",
        title = "Hyacinth",
        notes = "Two bulbs in a wicker pot, waiting for the flowers.",
        name = "Jacinta",
        wateringIntervalHours = 168,
        photos = mapOf(
            LocalDate.of(2026, 9, 28) to drawableUri(R.drawable.test_plant_3),
            LocalDate.of(2026, 10, 25) to drawableUri(R.drawable.test_plant_3),
        ),
    ),
    Plant(
        id = "00000000-0000-4000-8000-000000000004",
        title = "Unknown plant",
        notes = "",
        name = "Mystery",
        wateringIntervalHours = 96,
        photos = emptyMap(),
    ),
)
