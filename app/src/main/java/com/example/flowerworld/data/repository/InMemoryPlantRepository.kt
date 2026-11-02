package com.example.flowerworld.data.repository

import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Keeps plants in memory, so they are lost when the app process dies.
 * Temporary: replaced by a Room-backed implementation of [PlantRepository].
 */
class InMemoryPlantRepository(initialPlants: List<Plant> = emptyList()) : PlantRepository {
    private val _plants = MutableStateFlow(initialPlants)
    override val plants: Flow<List<Plant>> = _plants.asStateFlow()

    override suspend fun add(plant: Plant) {
        _plants.update { it + plant }
    }
}
