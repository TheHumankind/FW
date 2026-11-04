package com.example.flowerworld.feature.graveyard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.DeadPlantCard
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** The graveyard: a header and the list of plants the user has marked as dead. */
@Composable
fun GraveyardViewer(
    deadPlants: List<Plant>,
    onPlantClick: (Plant) -> Unit,
    onRevivePlant: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.graveyard_header).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.ListPadding, end = Dimens.ListPadding, top = Dimens.ListPadding)
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (deadPlants.isEmpty()) {
                AppCenteredMessage(text = stringResource(R.string.graveyard_empty))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Dimens.ListPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ListItemSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(deadPlants, key = { it.id }) { plant ->
                        DeadPlantCard(
                            plant = plant,
                            onClick = { onPlantClick(plant) },
                            onReviveClick = { onRevivePlant(plant) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GraveyardViewerPreview() {
    FlowerWorldTheme {
        GraveyardViewer(
            deadPlants = TestPlants.map { it.copy(diedAt = 1_790_000_000_000L) },
            onPlantClick = {},
            onRevivePlant = {}
        )
    }
}
