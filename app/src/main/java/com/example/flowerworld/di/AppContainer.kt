package com.example.flowerworld.di

import android.content.Context
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.data.repository.InMemoryPlantRepository
import com.example.flowerworld.data.storage.FilePhotoStorage
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.storage.PhotoStorage
import com.example.flowerworld.domain.usecase.AddPlantUseCase

/**
 * Manual dependency container: creates the app-wide singletons once and hands them to ViewModels.
 * Replace with Koin or Hilt when the graph grows (only on the user's command).
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val plantRepository: PlantRepository by lazy { InMemoryPlantRepository(TestPlants) } // TODO: Room
    val photoStorage: PhotoStorage by lazy { FilePhotoStorage(appContext) }
    val addPlantUseCase: AddPlantUseCase by lazy { AddPlantUseCase(plantRepository, photoStorage) }
}
