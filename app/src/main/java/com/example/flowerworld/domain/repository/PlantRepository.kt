package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.Plant
import kotlinx.coroutines.flow.Flow

/** Single source of truth for the user's plants. The UI only observes [plants] and calls [add]. */
interface PlantRepository {
    /** Living plants. */
    val plants: Flow<List<Plant>>

    /** The graveyard: plants the user marked as dead, the most recent first. */
    val deadPlants: Flow<List<Plant>>

    suspend fun add(plant: Plant)

    /** Records that the plant with [plantId] was watered just now. */
    suspend fun markWatered(plantId: String)

    /** Moves the plant with [plantId] from [plants] to [deadPlants]. */
    suspend fun markDead(plantId: String)
}
