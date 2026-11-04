package com.example.flowerworld.data.local

import androidx.room.ColumnInfo
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
    // the column keeps its old name: SQLite on older Android cannot rename columns, so no migration is needed
    @ColumnInfo(name = "description") val notes: String,
    val name: String,
    val wateringIntervalHours: Int,
    val lastWateredAt: Long,
    val diedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
)

/** One photo of a plant, added at [takenAt] (epoch millis). A deleted photo stays as a row until the server knows about it. */
@Entity(
    tableName = "plant_photos",
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
    @PrimaryKey val id: String,
    val plantId: String,
    val takenAt: Long,
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
