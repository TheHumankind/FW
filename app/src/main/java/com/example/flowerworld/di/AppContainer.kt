package com.example.flowerworld.di

import android.content.Context
import androidx.room.Room
import com.example.flowerworld.data.local.FlowerWorldDatabase
import com.example.flowerworld.data.repository.PrefsSettingsRepository
import com.example.flowerworld.data.repository.RoomPlantRepository
import com.example.flowerworld.data.storage.FilePhotoStorage
import com.example.flowerworld.domain.repository.PlantRepository
import com.example.flowerworld.domain.repository.SettingsRepository
import com.example.flowerworld.domain.storage.PhotoStorage
import com.example.flowerworld.domain.usecase.AddPlantPhotoUseCase
import com.example.flowerworld.domain.usecase.AddPlantUseCase
import com.example.flowerworld.domain.usecase.DeletePlantPhotoUseCase
import com.example.flowerworld.reminder.ReminderScheduler
import com.example.flowerworld.BuildConfig
import com.example.flowerworld.data.remote.TreflePlantCatalog
import com.example.flowerworld.domain.repository.PlantCatalogRepository

/**
 * Manual dependency container: creates the app-wide singletons once and hands them to ViewModels.
 * Replace with Koin or Hilt when the graph grows (only on the user's command).
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: FlowerWorldDatabase by lazy {
        Room.databaseBuilder(appContext, FlowerWorldDatabase::class.java, FlowerWorldDatabase.Name)
            .addMigrations(
                FlowerWorldDatabase.Migration1To2,
                FlowerWorldDatabase.Migration2To3,
                FlowerWorldDatabase.Migration3To4
            )
            .build()
    }

    val plantRepository: PlantRepository by lazy { RoomPlantRepository(database.plantDao()) }
    val reminderScheduler: ReminderScheduler by lazy { ReminderScheduler(appContext, settingsRepository) }
    val settingsRepository: SettingsRepository by lazy { PrefsSettingsRepository(appContext) }
    val plantCatalog: PlantCatalogRepository by lazy { TreflePlantCatalog(BuildConfig.TREFLE_TOKEN) }
    val photoStorage: PhotoStorage by lazy { FilePhotoStorage(appContext) }
    val addPlantUseCase: AddPlantUseCase by lazy { AddPlantUseCase(plantRepository, photoStorage) }
    val addPlantPhotoUseCase: AddPlantPhotoUseCase by lazy { AddPlantPhotoUseCase(plantRepository, photoStorage) }
    val deletePlantPhotoUseCase: DeletePlantPhotoUseCase by lazy { DeletePlantPhotoUseCase(plantRepository, photoStorage) }
}
