package com.example.flowerworld.di

import android.content.Context
import androidx.room.Room
import com.example.flowerworld.data.local.FlowerWorldDatabase
import com.example.flowerworld.data.repository.RoomPlantRepository
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

    private val database: FlowerWorldDatabase by lazy {
        Room.databaseBuilder(appContext, FlowerWorldDatabase::class.java, FlowerWorldDatabase.Name).build()
    }

    val plantRepository: PlantRepository by lazy { RoomPlantRepository(database.plantDao()) }
    val photoStorage: PhotoStorage by lazy { FilePhotoStorage(appContext) }
    val addPlantUseCase: AddPlantUseCase by lazy { AddPlantUseCase(plantRepository, photoStorage) }
}
