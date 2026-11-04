package com.example.flowerworld.feature.settings

import androidx.annotation.StringRes
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.PlantHeading

/** Name of the option in the selector: the same words as the "Title" and "Name" fields of the plant form. */
@get:StringRes
val PlantHeading.nameRes: Int
    get() = when (this) {
        PlantHeading.Title -> R.string.field_title
        PlantHeading.Name -> R.string.field_name
    }
