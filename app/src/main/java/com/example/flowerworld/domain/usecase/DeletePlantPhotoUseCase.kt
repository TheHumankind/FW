package com.example.flowerworld.domain.usecase

import com.example.flowerworld.domain.model.PlantPhoto
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.storage.PhotoStorage

/** Removes a photo from its plant and deletes its file. */
class DeletePlantPhotoUseCase(
    private val plantRepository: PlantRepository,
    private val photoStorage: PhotoStorage,
) {
    suspend operator fun invoke(photo: PlantPhoto) {
        // the row goes first: a missing file is harmless, a row pointing at a missing file is not
        plantRepository.deletePhoto(photo.id)
        photoStorage.delete(photo.path)
    }
}
