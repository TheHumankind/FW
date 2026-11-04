package com.example.flowerworld.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Shows [content]; a long press on it opens a small menu with [menu] whose top-right corner is under
 * the finger (like the menu of a plant card). [menu] gets a function that closes it; use [MenuCardItem] for its lines.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LongPressMenuBox(
    menu: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    // where the finger last touched, in this box's own coordinates
    var touchPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            // only watches the touch-down (initial pass); the long press handling below still gets every event
            .pointerInput(Unit) {
                awaitEachGesture {
                    touchPosition = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial).position
                }
            }
            .combinedClickable(onClick = {}, onLongClick = { menuExpanded = true })
    ) {
        content()
        if (menuExpanded) {
            TouchPointMenu(
                touchPosition = touchPosition,
                onDismiss = { menuExpanded = false },
                content = { menu { menuExpanded = false } }
            )
        }
    }
}
