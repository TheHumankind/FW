package com.example.flowerworld.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.ImageVector

/** A tombstone with a cross: the Material set has no such icon, so it is drawn here (24 x 24 grid). */
val Icons.Filled.Tombstone: ImageVector by lazy {
    materialIcon(name = "Filled.Tombstone") {
        materialPath(pathFillType = PathFillType.EvenOdd) {
            // slab with a rounded top
            moveTo(6f, 20f)
            verticalLineTo(9f)
            arcTo(6f, 6f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18f, 9f)
            verticalLineTo(20f)
            close()
            // cross cut out of the slab
            moveTo(11f, 7f)
            horizontalLineTo(13f)
            verticalLineTo(9f)
            horizontalLineTo(15f)
            verticalLineTo(11f)
            horizontalLineTo(13f)
            verticalLineTo(15f)
            horizontalLineTo(11f)
            verticalLineTo(11f)
            horizontalLineTo(9f)
            verticalLineTo(9f)
            horizontalLineTo(11f)
            close()
            // ground line
            moveTo(3f, 21f)
            horizontalLineTo(21f)
            verticalLineTo(23f)
            horizontalLineTo(3f)
            close()
        }
    }
}
