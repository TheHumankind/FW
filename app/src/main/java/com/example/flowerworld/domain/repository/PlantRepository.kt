package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.Plant
import kotlinx.coroutines.flow.Flow

/** Single source of truth for the user's plants. The UI only observes [plants] and calls [add]. */
interface PlantRepository {
    val plants: Flow<List<Plant>>

    suspend fun add(plant: Plant)
}
