package com.example.flowerworld.data.repository

import com.example.flowerworld.data.local.PlantDao
import com.example.flowerworld.data.local.toDomain
import com.example.flowerworld.data.local.toEntity
import com.example.flowerworld.data.local.toPhotoEntities
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

/** Stores plants in the local Room database; the UI only ever reads from here (local-first). */
class RoomPlantRepository(
    private val plantDao: PlantDao,
    private val clock: Clock = Clock.systemDefaultZone(),
) : PlantRepository {

    override val plants: Flow<List<Plant>> =
        plantDao.observePlants().map { rows -> rows.map { it.toDomain() } }

    override val deadPlants: Flow<List<Plant>> =
        plantDao.observeDeadPlants().map { rows -> rows.map { it.toDomain() } }

    override fun plant(plantId: String): Flow<Plant?> =
        plantDao.observePlant(plantId).map { it?.toDomain() }

    override suspend fun add(plant: Plant) {
        val now = clock.millis()
        plantDao.insert(plant.toEntity(now), plant.toPhotoEntities(now))
    }

    override suspend fun updateDetails(
        plantId: String,
        title: String,
        name: String,
        notes: String,
        wateringIntervalHours: Int
    ) {
        plantDao.updateDetails(plantId, title, name, notes, wateringIntervalHours, clock.millis())
    }

    override suspend fun markWatered(plantId: String) {
        plantDao.markWatered(plantId, clock.millis())
    }

    override suspend fun revive(plantId: String) {
        plantDao.markAlive(plantId, clock.millis())
    }

    override suspend fun markDead(plantId: String) {
        plantDao.markDead(plantId, clock.millis())
    }
}
