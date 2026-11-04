package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import kotlin.math.roundToInt

private val CardHeight = 88.dp
private val CardShape = RoundedCornerShape(12.dp)
private val MenuMinWidth = 112.dp

/**
 * Thin horizontal card of a plant: the latest photo on the left,
 * the name and the time left until the next watering on the right. Tap opens the plant,
 * long press opens a menu with "Watered" and "Delete" whose top-right corner is under the finger.
 */
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onWateredClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    // where the finger last touched the card, in the card's own coordinates
    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CardHeight)
                .clip(CardShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                // only watches the touch-down (initial pass), the click handling below still gets every event
                .pointerInput(Unit) {
                    awaitEachGesture {
                        touchPosition = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).position
                    }
                }
                .combinedClickable(onClick = onClick, onLongClick = { menuExpanded = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PhotoBox(
                photoUri = plant.latestPhoto,
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
        if (menuExpanded) {
            PlantCardMenu(
                touchPosition = touchPosition,
                onDismiss = { menuExpanded = false },
                onWateredClick = {
                    menuExpanded = false
                    onWateredClick()
                },
                onDeleteClick = {
                    menuExpanded = false
                    onDeleteClick()
                }
            )
        }
    }
}

/**
 * Small menu shown over the card with its top-right corner at [touchPosition]
 * (relative to the card). It is kept inside the window if the finger is near an edge.
 */
@Composable
private fun PlantCardMenu(
    touchPosition: Offset,
    onDismiss: () -> Unit,
    onWateredClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val positionProvider = remember(touchPosition) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val right = anchorBounds.left + touchPosition.x.roundToInt()
                val top = anchorBounds.top + touchPosition.y.roundToInt()
                return IntOffset(
                    x = (right - popupContentSize.width).coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
                    y = top.coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0))
                )
            }
        }
    }
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            tonalElevation = 3.dp
        ) {
            Column(modifier = Modifier.width(IntrinsicSize.Max).padding(vertical = 4.dp)) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_watered)) },
                    onClick = onWateredClick,
                    modifier = Modifier.width(MenuMinWidth)
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.delete_plant),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = onDeleteClick,
                    modifier = Modifier.width(MenuMinWidth)
                )
            }
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
        PlantCard(plant = TestPlants.first(), onClick = {}, onWateredClick = {}, onDeleteClick = {})
    }
}
