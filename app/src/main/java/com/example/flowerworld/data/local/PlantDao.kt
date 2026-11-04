package com.example.flowerworld.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    /** Living plants (not deleted, not dead), oldest first. Emits again whenever the tables change. */
    @Transaction
    @Query("SELECT * FROM plants WHERE isDeleted = 0 AND diedAt IS NULL ORDER BY createdAt, id")
    fun observePlants(): Flow<List<PlantWithPhotos>>

    /** Dead plants (the graveyard), the most recently dead first. */
    @Transaction
    @Query("SELECT * FROM plants WHERE isDeleted = 0 AND diedAt IS NOT NULL ORDER BY diedAt DESC, id")
    fun observeDeadPlants(): Flow<List<PlantWithPhotos>>

    /** One plant, alive or dead, or null if it does not exist. Emits again whenever it changes. */
    @Transaction
    @Query("SELECT * FROM plants WHERE id = :id AND isDeleted = 0")
    fun observePlant(id: String): Flow<PlantWithPhotos?>

    /** Brings the plant back from the graveyard; also bumps the sync modification time. */
    @Query("UPDATE plants SET diedAt = NULL, updatedAt = :revivedAt WHERE id = :id AND isDeleted = 0")
    suspend fun markAlive(id: String, revivedAt: Long)

    /** Moves the plant to the graveyard at [diedAt] (epoch millis); also bumps the sync modification time. */
    @Query("UPDATE plants SET diedAt = :diedAt, updatedAt = :diedAt WHERE id = :id AND isDeleted = 0")
    suspend fun markDead(id: String, diedAt: Long)

    /** Saves the user-editable details; also bumps the sync modification time. */
    @Query(
        "UPDATE plants SET title = :title, name = :name, description = :notes, " +
            "wateringIntervalHours = :wateringIntervalHours, updatedAt = :updatedAt WHERE id = :id AND isDeleted = 0"
    )
    suspend fun updateDetails(
        id: String,
        title: String,
        name: String,
        notes: String,
        wateringIntervalHours: Int,
        updatedAt: Long,
    )

    /** Records a watering at [wateredAt] (epoch millis); also bumps the sync modification time. */
    @Query("UPDATE plants SET lastWateredAt = :wateredAt, updatedAt = :wateredAt WHERE id = :id AND isDeleted = 0")
    suspend fun markWatered(id: String, wateredAt: Long)

    @Insert
    suspend fun insertPhoto(photo: PlantPhotoEntity)

    /** Marks the photo as deleted; also bumps the sync modification time. */
    @Query("UPDATE plant_photos SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markPhotoDeleted(id: String, updatedAt: Long)

    @Insert
    suspend fun insertPlant(plant: PlantEntity)

    @Insert
    suspend fun insertPhotos(photos: List<PlantPhotoEntity>)

    /** Saves a plant and its photos together: either all rows are written or none. */
    @Transaction
    suspend fun insert(plant: PlantEntity, photos: List<PlantPhotoEntity>) {
        insertPlant(plant)
        insertPhotos(photos)
    }
}
