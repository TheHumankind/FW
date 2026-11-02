package com.example.flowerworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flowerworld.ui.theme.DebugRed
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Round button. Without [onClick] it is inactive: just a colored circle. */
@Composable
fun AppRoundButton(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null
) {
    val shape = Modifier
        .size(size)
        .clip(CircleShape)
        .background(color)
    val clickable = if (onClick != null) {
        shape.clickable(role = Role.Button, onClick = onClick)
    } else {
        shape
    }
    Box(modifier = modifier.then(clickable))
}

@Preview(showBackground = true)
@Composable
private fun AppRoundButtonPreview() {
    FlowerWorldTheme {
        AppRoundButton(color = DebugRed)
    }
}
