package com.example.flowerworld.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.ui.formatDate
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/**
 * Card of a dead plant in the graveyard: the last photo, the name and the date it died.
 * Tap opens the plant; long press opens a menu with "Revive" in case it was marked dead by mistake.
 */
@Composable
fun DeadPlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onReviveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diedText = plant.diedAt?.let { AnnotatedString(stringResource(R.string.died_on, formatDate(it))) }
    MenuCard(
        onClick = onClick,
        modifier = modifier,
        menu = { dismiss ->
            MenuCardItem(
                text = stringResource(R.string.revive_plant),
                onClick = {
                    dismiss()
                    onReviveClick()
                }
            )
        }
    ) {
        PlantCardBody(plant = plant, bottomText = diedText)
    }
}

@Preview(showBackground = true)
@Composable
private fun DeadPlantCardPreview() {
    FlowerWorldTheme {
        DeadPlantCard(
            plant = TestPlants.first().copy(diedAt = 1_790_000_000_000L),
            onClick = {},
            onReviveClick = {}
        )
    }
}
