package com.example.flowerworld.feature.plants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Shows the user's plants. Placeholder for now: only a text. */
@Composable
fun PlantViewer(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.plant_viewer_placeholder),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantViewerPreview() {
    FlowerWorldTheme {
        PlantViewer()
    }
}
