package com.example.flowerworld.feature.plants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Shows the user's plants. Placeholder for now: a text and the "Add plant" button at the bottom. */
@Composable
fun PlantViewer(
    onAddPlantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.plant_viewer_placeholder),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )
        AppIconButton(
            icon = Icons.Filled.AddCircle,
            contentDescription = stringResource(R.string.add_plant),
            onClick = onAddPlantClick,
            size = 36.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantViewerPreview() {
    FlowerWorldTheme {
        PlantViewer(onAddPlantClick = {})
    }
}
