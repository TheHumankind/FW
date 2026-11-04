package com.example.flowerworld.domain.usecase

import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.model.PlantDraft
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.storage.PhotoStorage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import java.util.UUID

/** Creates a plant from a [PlantDraft]: stores its photo (if any) and saves the plant. */
class AddPlantUseCase(
    private val plantRepository: PlantRepository,
    private val photoStorage: PhotoStorage,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    /** @throws java.io.IOException if the photo cannot be stored; nothing is saved then. */
    suspend operator fun invoke(draft: PlantDraft) {
        val photos = draft.photoSourceUri
            ?.let { photoStorage.import(it) }
            ?.let { path -> mapOf(LocalDate.now(clock) to path) }
            .orEmpty()
        val plant = Plant(
            id = UUID.randomUUID().toString(),
            title = draft.title,
            notes = draft.notes,
            name = draft.name,
            wateringIntervalHours = draft.wateringIntervalHours,
            photos = photos,
            lastWateredAt = clock.millis(),
        )
        try {
            plantRepository.add(plant)
        } catch (e: Exception) {
            // do not leave an orphan file behind when saving the plant failed
            withContext(NonCancellable) { photos.values.forEach { photoStorage.delete(it) } }
            throw e
        }
    }
}
