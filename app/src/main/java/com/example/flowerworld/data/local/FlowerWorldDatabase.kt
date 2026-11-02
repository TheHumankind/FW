package com.example.flowerworld.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlantEntity::class, PlantPhotoEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class FlowerWorldDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        const val Name = "flowerworld.db"
    }
}
