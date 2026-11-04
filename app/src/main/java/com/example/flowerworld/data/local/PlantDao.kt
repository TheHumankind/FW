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

    /** Moves the plant to the graveyard at [diedAt] (epoch millis); also bumps the sync modification time. */
    @Query("UPDATE plants SET diedAt = :diedAt, updatedAt = :diedAt WHERE id = :id AND isDeleted = 0")
    suspend fun markDead(id: String, diedAt: Long)

    /** Records a watering at [wateredAt] (epoch millis); also bumps the sync modification time. */
    @Query("UPDATE plants SET lastWateredAt = :wateredAt, updatedAt = :wateredAt WHERE id = :id AND isDeleted = 0")
    suspend fun markWatered(id: String, wateredAt: Long)

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
