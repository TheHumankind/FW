package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.model.PlantPhoto
import kotlinx.coroutines.flow.Flow

/** Single source of truth for the user's plants. The UI only observes [plants] and calls [add]. */
interface PlantRepository {
    /** Living plants. */
    val plants: Flow<List<Plant>>

    /** The graveyard: plants the user marked as dead, the most recent first. */
    val deadPlants: Flow<List<Plant>>

    /** One plant, alive or dead; emits null if there is no such plant. */
    fun plant(plantId: String): Flow<Plant?>

    suspend fun add(plant: Plant)

    /** Replaces the details the user can edit; the next watering time follows from [wateringIntervalHours]. */
    suspend fun updateDetails(plantId: String, title: String, name: String, notes: String, wateringIntervalHours: Int)

    /** Adds [photo] to the plant with [plantId]. */
    suspend fun addPhoto(plantId: String, photo: PlantPhoto)

    /** Removes the photo with [photoId] from its plant; the file itself is the caller's to delete. */
    suspend fun deletePhoto(photoId: String)

    /** Records that the plant with [plantId] was watered just now. */
    suspend fun markWatered(plantId: String)

    /** Moves the plant with [plantId] from [plants] to [deadPlants]. */
    suspend fun markDead(plantId: String)

    /** Moves the plant with [plantId] back from [deadPlants] to [plants], as it was. */
    suspend fun revive(plantId: String)
}
