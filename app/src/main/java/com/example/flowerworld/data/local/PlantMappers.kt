package com.example.flowerworld.data.local

import com.example.flowerworld.domain.model.Plant
import java.time.LocalDate

/** Rows without the deleted ones turned into the domain model. */
fun PlantWithPhotos.toDomain(): Plant = Plant(
    id = plant.id,
    title = plant.title,
    description = plant.description,
    name = plant.name,
    wateringIntervalHours = plant.wateringIntervalHours,
    lastWateredAt = plant.lastWateredAt,
    photos = photos
        .filterNot { it.isDeleted }
        .associate { LocalDate.ofEpochDay(it.dateEpochDay) to it.path },
)

/** A new plant as a row; [nowMillis] becomes both its creation and modification time. */
fun Plant.toEntity(nowMillis: Long): PlantEntity = PlantEntity(
    id = id,
    title = title,
    description = description,
    name = name,
    wateringIntervalHours = wateringIntervalHours,
    lastWateredAt = lastWateredAt,
    createdAt = nowMillis,
    updatedAt = nowMillis,
)

fun Plant.toPhotoEntities(nowMillis: Long): List<PlantPhotoEntity> = photos.map { (date, path) ->
    PlantPhotoEntity(
        plantId = id,
        dateEpochDay = date.toEpochDay(),
        path = path,
        updatedAt = nowMillis,
    )
}
