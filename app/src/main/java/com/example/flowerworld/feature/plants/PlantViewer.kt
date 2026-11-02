package com.example.flowerworld.feature.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.components.PlantCard
import com.example.flowerworld.ui.theme.FlowerWorldTheme

private val AddButtonSize = 36.dp
private val AddButtonPadding = 16.dp

/** Shows the user's plants as a vertical list of cards, with the "Add plant" button at the bottom. */
@Composable
fun PlantViewer(
    plants: List<Plant>,
    onAddPlantClick: () -> Unit,
    onEditPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (plants.isEmpty()) {
            Text(
                text = stringResource(R.string.plant_viewer_placeholder),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    // leave room for the add button that floats over the list
                    bottom = AddButtonSize + AddButtonPadding * 2
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantCard(plant = plant, onEditClick = { onEditPlantClick(plant) })
                }
            }
        }
        AppIconButton(
            icon = Icons.Filled.AddCircle,
            contentDescription = stringResource(R.string.add_plant),
            onClick = onAddPlantClick,
            size = AddButtonSize,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = AddButtonPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantViewerPreview() {
    FlowerWorldTheme {
        PlantViewer(plants = TestPlants, onAddPlantClick = {}, onEditPlantClick = {})
    }
}
