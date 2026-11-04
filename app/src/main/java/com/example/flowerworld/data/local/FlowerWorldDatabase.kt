package com.example.flowerworld.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(
    entities = [PlantEntity::class, PlantPhotoEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class FlowerWorldDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        const val Name = "flowerworld.db"

        /** Adds the last watering time; existing plants count as watered when they were added. */
        val Migration1To2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE plants ADD COLUMN lastWateredAt INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("UPDATE plants SET lastWateredAt = createdAt")
            }
        }
    }
}
