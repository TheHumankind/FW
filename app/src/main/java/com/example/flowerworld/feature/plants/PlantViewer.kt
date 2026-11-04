package com.example.flowerworld.feature.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.components.PlantCard
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

private val AddButtonSize = 36.dp
private val AddButtonPadding = 16.dp

/** Shows the user's plants as a vertical list of cards, with the "Add plant" button at the bottom. */
@Composable
fun PlantViewer(
    plants: List<Plant>,
    onAddPlantClick: () -> Unit,
    onPlantClick: (Plant) -> Unit,
    onWaterPlant: (Plant) -> Unit,
    onDeletePlant: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.plants_header).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.ListPadding, end = Dimens.ListPadding, top = Dimens.ListPadding)
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            PlantList(plants, onAddPlantClick, onPlantClick, onWaterPlant, onDeletePlant)
        }
    }
}

@Composable
private fun PlantList(
    plants: List<Plant>,
    onAddPlantClick: () -> Unit,
    onPlantClick: (Plant) -> Unit,
    onWaterPlant: (Plant) -> Unit,
    onDeletePlant: (Plant) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (plants.isEmpty()) {
            AppCenteredMessage(text = stringResource(R.string.plant_viewer_placeholder))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = Dimens.ListPadding,
                    end = Dimens.ListPadding,
                    top = Dimens.ListPadding,
                    // leave room for the add button that floats over the list
                    bottom = AddButtonSize + AddButtonPadding * 2
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant = plant,
                        onClick = { onPlantClick(plant) },
                        onWateredClick = { onWaterPlant(plant) },
                        onDeleteClick = { onDeletePlant(plant) }
                    )
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
        PlantViewer(plants = TestPlants, onAddPlantClick = {}, onPlantClick = {}, onWaterPlant = {}, onDeletePlant = {})
    }
}
