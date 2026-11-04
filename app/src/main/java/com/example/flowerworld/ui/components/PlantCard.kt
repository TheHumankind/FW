package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.theme.FlowerWorldTheme

private val CardHeight = 88.dp
private val CardShape = RoundedCornerShape(12.dp)

/**
 * Thin horizontal card of a plant: the latest photo on the left,
 * the name and the watering interval on the right, the edit button at the end.
 */
@Composable
fun PlantCard(
    plant: Plant,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photo = plant.photos.maxByOrNull { it.key }?.value
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(CardShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhotoBox(
            photoUri = photo,
            contentDescription = plant.name,
            placeholder = Icons.Filled.Eco,
            placeholderTint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .size(CardHeight)
                .background(MaterialTheme.colorScheme.secondary)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pluralStringResource(
                    R.plurals.watering_interval_hours,
                    plant.wateringIntervalHours,
                    plant.wateringIntervalHours
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        AppIconButton(
            icon = Icons.Filled.Edit,
            contentDescription = stringResource(R.string.edit_plant),
            onClick = onEditClick,
            size = 28.dp,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantCardPreview() {
    FlowerWorldTheme {
        PlantCard(plant = TestPlants.first(), onEditClick = {})
    }
}
