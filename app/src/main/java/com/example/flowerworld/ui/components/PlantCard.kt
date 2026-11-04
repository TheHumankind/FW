package com.example.flowerworld.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Card of a living plant: the latest photo, the name and the time left until the next watering.
 * Tap opens the plant, long press opens a menu with "Watered" and "Dead" under the finger.
 */
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onWateredClick: () -> Unit,
    onDeadClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeUnit: IntervalUnit = IntervalUnit.Days
) {
    val now = remember(plant) { System.currentTimeMillis() }
    val timeLeft = remember(plant, timeUnit) { plant.timeUntilNextWatering(now, timeUnit) }
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
        PlantCardBody(
            plant = plant,
            bottomText = if (plant.isWateringDue(now)) {
                AnnotatedString(stringResource(R.string.next_watering_overdue))
            } else {
                nextWateringText(timeLeft, timeUnit)
            }
        )
    }
}

/** "9 hours until next watering" (or days) with only the time ("9 hours") in bold. */
@Composable
private fun nextWateringText(count: Int, unit: IntervalUnit): AnnotatedString {
    val plural = when (unit) {
        IntervalUnit.Days -> R.plurals.days_count
        IntervalUnit.Hours -> R.plurals.hours_count
    }
    val duration = pluralStringResource(plural, count, count)
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
