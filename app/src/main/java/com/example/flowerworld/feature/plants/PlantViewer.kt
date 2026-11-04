package com.example.flowerworld.feature.plants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.components.AppListScreen
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
    onPlantDied: (Plant) -> Unit,
    timeUnit: IntervalUnit,
    modifier: Modifier = Modifier
) {
    AppListScreen(title = stringResource(R.string.plants_header), modifier = modifier) {
        if (plants.isEmpty()) {
            AppCenteredMessage(text = stringResource(R.string.plant_viewer_placeholder))
        } else {
            PlantList(plants, timeUnit, onPlantClick, onWaterPlant, onPlantDied)
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

@Composable
private fun BoxScope.PlantList(
    plants: List<Plant>,
    timeUnit: IntervalUnit,
    onPlantClick: (Plant) -> Unit,
    onWaterPlant: (Plant) -> Unit,
    onPlantDied: (Plant) -> Unit
) {
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
                onDeadClick = { onPlantDied(plant) },
                timeUnit = timeUnit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantViewerPreview() {
    FlowerWorldTheme {
        PlantViewer(plants = TestPlants, onAddPlantClick = {}, onPlantClick = {}, onWaterPlant = {}, onPlantDied = {},
            timeUnit = IntervalUnit.Hours
        )
    }
}
