package com.example.flowerworld.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

@Database(
    entities = [PlantEntity::class, PlantPhotoEntity::class],
    version = 4,
    exportSchema = true,
)
abstract class FlowerWorldDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        const val Name = "flowerworld.db"

        /**
         * Photos get their own id and the time they were added instead of one photo per day, so a plant
         * can have several photos a day. Existing photos keep their file; their time is the start of their day.
         */
        val Migration3To4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "CREATE TABLE `plant_photos_new` (`id` TEXT NOT NULL, `plantId` TEXT NOT NULL, " +
                        "`takenAt` INTEGER NOT NULL, `path` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                        "`isDeleted` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
                        "FOREIGN KEY(`plantId`) REFERENCES `plants`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                connection.execSQL(
                    "INSERT INTO plant_photos_new (id, plantId, takenAt, path, updatedAt, isDeleted) " +
                        "SELECT plantId || '-' || dateEpochDay, plantId, dateEpochDay * 86400000, path, updatedAt, isDeleted " +
                        "FROM plant_photos"
                )
                connection.execSQL("DROP TABLE plant_photos")
                connection.execSQL("ALTER TABLE plant_photos_new RENAME TO plant_photos")
                connection.execSQL("CREATE INDEX IF NOT EXISTS `index_plant_photos_plantId` ON `plant_photos` (`plantId`)")
            }
        }

        /** Adds the time a plant died; null means alive. */
        val Migration2To3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE plants ADD COLUMN diedAt INTEGER")
            }
        }

        /** Adds the last watering time; existing plants count as watered when they were added. */
        val Migration1To2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE plants ADD COLUMN lastWateredAt INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("UPDATE plants SET lastWateredAt = createdAt")
            }
        }
    }
}
