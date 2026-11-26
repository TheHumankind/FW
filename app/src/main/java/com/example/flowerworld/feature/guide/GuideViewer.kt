package com.example.flowerworld.feature.guide

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.ui.components.AppListScreen

/**
 * Footer tab for finding plants: the bundled plant guide (works offline, has care data).
 * [onSpeciesClick] gets the entry the user picked.
 *
 * The online catalog search ([OnlineSearch], Trefle) is built and kept, but not shown for now: the
 * guide fits the app better. To bring it back, show [OnlineSearch] again next to [GuideSearch].
 */
@Composable
fun GuideViewer(
    onSpeciesClick: (PlantSpecies) -> Unit,
    modifier: Modifier = Modifier
) {
    AppListScreen(title = stringResource(R.string.search_title), modifier = modifier) {
        GuideSearch(onSpeciesClick = onSpeciesClick)
    }
}

/** Colors of a chip that must be easy to see when it is on. */
@Composable
internal fun selectableChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
)
