package com.example.flowerworld.feature.search

import androidx.annotation.StringRes
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.PlantColor

/** The label of a color in the filters. */
@get:StringRes
val PlantColor.nameRes: Int
    get() = when (this) {
        PlantColor.White -> R.string.color_white
        PlantColor.Red -> R.string.color_red
        PlantColor.Brown -> R.string.color_brown
        PlantColor.Orange -> R.string.color_orange
        PlantColor.Yellow -> R.string.color_yellow
        PlantColor.Lime -> R.string.color_lime
        PlantColor.Green -> R.string.color_green
        PlantColor.Cyan -> R.string.color_cyan
        PlantColor.Blue -> R.string.color_blue
        PlantColor.Purple -> R.string.color_purple
        PlantColor.Magenta -> R.string.color_magenta
        PlantColor.Grey -> R.string.color_grey
        PlantColor.Black -> R.string.color_black
    }
