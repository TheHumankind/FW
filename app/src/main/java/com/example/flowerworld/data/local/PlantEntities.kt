package com.example.flowerworld.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Database row of a plant. Kept apart from the domain model and from any future network DTO.
 * [createdAt], [updatedAt] (epoch millis) and [isDeleted] are the sync fields: a deleted row stays
 * in the table until the server knows about it.
 */
@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val name: String,
    val wateringIntervalHours: Int,
    val lastWateredAt: Long,
    val diedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
)

/** One photo of a plant, taken on [dateEpochDay]; only one photo per plant per day. */
@Entity(
    tableName = "plant_photos",
    primaryKeys = ["plantId", "dateEpochDay"],
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("plantId")],
)
data class PlantPhotoEntity(
    val plantId: String,
    val dateEpochDay: Long,
    val path: String,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
)

/** A plant together with all its photo rows, loaded in one query. */
data class PlantWithPhotos(
    @Embedded val plant: PlantEntity,
    @Relation(parentColumn = "id", entityColumn = "plantId")
    val photos: List<PlantPhotoEntity>,
)
