package com.example.flowerworld.domain.usecase

import com.example.flowerworld.domain.model.PlantPhoto
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.storage.PhotoStorage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.time.Clock
import java.util.UUID

/** Stores a picked image and adds it to an existing plant as its newest photo. */
class AddPlantPhotoUseCase(
    private val plantRepository: PlantRepository,
    private val photoStorage: PhotoStorage,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    /** @throws java.io.IOException if the photo cannot be stored; nothing is saved then. */
    suspend operator fun invoke(plantId: String, sourceUri: String) {
        val path = photoStorage.import(sourceUri)
        try {
            plantRepository.addPhoto(plantId, PlantPhoto(UUID.randomUUID().toString(), path, clock.millis()))
        } catch (e: Exception) {
            // do not leave an orphan file behind when saving the photo failed
            withContext(NonCancellable) { photoStorage.delete(path) }
            throw e
        }
    }
}
