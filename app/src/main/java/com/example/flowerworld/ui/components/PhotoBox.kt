package com.example.flowerworld.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Fills its area with the photo at [photoUri] (a file path or a URI), cropped to fit.
 * Without a photo it shows [placeholder] in the middle. Size, shape and background come from [modifier].
 */
@Composable
fun PhotoBox(
    photoUri: String?,
    contentDescription: String?,
    placeholder: ImageVector,
    modifier: Modifier = Modifier,
    placeholderTint: Color = MaterialTheme.colorScheme.primary
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = placeholder,
                contentDescription = contentDescription,
                tint = placeholderTint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoBoxPreview() {
    FlowerWorldTheme {
        PhotoBox(
            photoUri = null,
            contentDescription = null,
            placeholder = Icons.Filled.Eco,
            modifier = Modifier.size(88.dp)
        )
    }
}
