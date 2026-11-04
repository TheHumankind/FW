package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flowerworld.domain.model.Plant

/**
 * Inside of a [MenuCard] row: the latest photo on the left; on the right the plant's name pinned
 * to the top and [bottomText] pinned to the bottom (nothing if it is null).
 */
@Composable
fun RowScope.PlantCardBody(
    plant: Plant,
    bottomText: AnnotatedString?
) {
    PhotoBox(
        photoUri = plant.latestPhoto,
        contentDescription = plant.name,
        placeholder = Icons.Filled.Eco,
        placeholderTint = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier
            .size(MenuCardHeight)
            .background(MaterialTheme.colorScheme.secondary)
    )
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = plant.name.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (bottomText != null) {
            Text(
                text = bottomText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
