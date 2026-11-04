package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Thin horizontal card of a plant: the latest photo on the left,
 * the name and the time left until the next watering on the right. Tap opens the plant,
 * long press opens a menu with "Watered" and "Dead" whose top-right corner is under the finger.
 */
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onWateredClick: () -> Unit,
    onDeadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MenuCard(
        onClick = onClick,
        modifier = modifier,
        menu = { dismiss ->
            MenuCardItem(
                text = stringResource(R.string.action_watered),
                onClick = {
                    dismiss()
                    onWateredClick()
                }
            )
            MenuCardItem(
                text = stringResource(R.string.mark_dead),
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    dismiss()
                    onDeadClick()
                }
            )
        }
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
            // the name is pinned to the top of the card, the watering line to the bottom
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
            val hoursLeft = remember(plant) { plant.hoursUntilNextWatering(System.currentTimeMillis()) }
            Text(
                text = if (hoursLeft > 0) {
                    nextWateringText(hoursLeft)
                } else {
                    AnnotatedString(stringResource(R.string.next_watering_overdue))
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** "9 hours until next watering" with only the time ("9 hours") in bold. */
@Composable
private fun nextWateringText(hours: Int): AnnotatedString {
    val duration = pluralStringResource(R.plurals.hours_count, hours, hours)
    // the translated sentence decides where the duration goes, so split it around a marker
    val parts = stringResource(R.string.next_watering_in, DurationMarker).split(DurationMarker)
    return buildAnnotatedString {
        append(parts.first())
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(duration) }
        append(parts.last())
    }
}

private const val DurationMarker = "\u0000"

@Preview(showBackground = true)
@Composable
private fun PlantCardPreview() {
    FlowerWorldTheme {
        PlantCard(plant = TestPlants.first(), onClick = {}, onWateredClick = {}, onDeadClick = {})
    }
}
