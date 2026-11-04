package com.example.flowerworld.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** A short message in the middle of the available space: an empty list, a placeholder, etc. */
@Composable
fun AppCenteredMessage(
    text: String,
    modifier: Modifier = Modifier,
    bold: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = if (bold) FontWeight.Bold else null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCenteredMessagePreview() {
    FlowerWorldTheme {
        AppCenteredMessage(text = "Nothing here yet", bold = true)
    }
}
