package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt

/** Height of every plant card; the square photo on the left is as wide as the card is high. */
val MenuCardHeight = 88.dp

private val CardShape = RoundedCornerShape(12.dp)
private val MenuMinWidth = 112.dp

/**
 * Shell of a plant card: a rounded row on the primary container color.
 * Tap calls [onClick]; a long press opens a small menu with [menu] whose top-right corner
 * is under the finger (kept inside the window near the edges). [menu] gets a function that closes it.
 */
@Composable
fun MenuCard(
    onClick: () -> Unit,
    menu: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    // where the finger last touched the card, in the card's own coordinates
    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MenuCardHeight)
                .clip(CardShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                // only watches the touch-down (initial pass), the click handling below still gets every event
                .pointerInput(Unit) {
                    awaitEachGesture {
                        touchPosition = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).position
                    }
                }
                .combinedClickable(onClick = onClick, onLongClick = { menuExpanded = true }),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
        if (menuExpanded) {
            TouchPointMenu(
                touchPosition = touchPosition,
                onDismiss = { menuExpanded = false },
                content = { menu { menuExpanded = false } }
            )
        }
    }
}

/** One line of the card menu. */
@Composable
fun MenuCardItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    DropdownMenuItem(
        text = { Text(text = text, color = color) },
        onClick = onClick,
        modifier = modifier.width(MenuMinWidth)
    )
}

/** Popup whose top-right corner is at [touchPosition] (relative to the parent) and that stays inside the window. */
@Composable
private fun TouchPointMenu(
    touchPosition: Offset,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
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
            Column(modifier = Modifier.width(IntrinsicSize.Max).padding(vertical = 4.dp), content = content)
        }
    }
}
