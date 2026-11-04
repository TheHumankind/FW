package com.example.flowerworld.feature.plantinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.components.AppScreen
import com.example.flowerworld.ui.components.PhotoBox
import com.example.flowerworld.ui.formatDateTime
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

private val PhotoHeight = 240.dp
private val BackButtonSize = 36.dp
private val BackButtonPadding = 16.dp

/** Everything known about one plant, as a column: the latest photo on top, then the details. */
@Composable
fun PlantInfoScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlantInfoViewModel = viewModel(factory = PlantInfoViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlantInfoContent(state = state, onBackClick = onBackClick, modifier = modifier)
}

@Composable
private fun PlantInfoContent(
    state: PlantInfoUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                PlantInfoUiState.Loading -> Unit
                PlantInfoUiState.Error -> AppCenteredMessage(text = stringResource(R.string.plant_not_found))
                is PlantInfoUiState.Success -> PlantDetails(state.plant)
            }
            // sticky: stays in the bottom-right corner while the details scroll, within thumb reach
            AppIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                onClick = onBackClick,
                size = BackButtonSize,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(BackButtonPadding)
            )
        }
    }
}

@Composable
private fun PlantDetails(plant: Plant) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PhotoBox(
            photoUri = plant.latestPhoto,
            contentDescription = plant.name,
            placeholder = Icons.Filled.Eco,
            modifier = Modifier
                .fillMaxWidth()
                .height(PhotoHeight)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenHorizontalPadding)
                // leave room for the back button that floats over the bottom of the list
                .navigationBarsPadding()
                .padding(bottom = BackButtonSize + BackButtonPadding * 2),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(stringResource(R.string.field_name), plant.name)
            InfoRow(stringResource(R.string.field_title), plant.title)
            if (plant.description.isNotBlank()) {
                InfoRow(stringResource(R.string.field_description), plant.description)
            }
            InfoRow(
                stringResource(R.string.field_watering_interval),
                plant.wateringIntervalHours.toString()
            )
            InfoRow(stringResource(R.string.info_last_watered), formatDateTime(plant.lastWateredAt))
            // a dead plant has no next watering, it has a date of death instead
            plant.diedAt
                ?.let { InfoRow(stringResource(R.string.info_died), formatDateTime(it)) }
                ?: InfoRow(stringResource(R.string.info_next_watering), formatDateTime(plant.nextWateringAt))
        }
    }
}

/** A small label with its value underneath. */
@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}


@Preview(showBackground = true)
@Composable
private fun PlantInfoContentPreview() {
    FlowerWorldTheme {
        PlantInfoContent(
            state = PlantInfoUiState.Success(TestPlants.first()),
            onBackClick = {}
        )
    }
}
