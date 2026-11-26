package com.example.flowerworld.feature.species

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.PlantSpecies
import com.example.flowerworld.feature.search.GuideResults
import com.example.flowerworld.feature.search.SpeciesSearchViewModel
import com.example.flowerworld.feature.search.appLanguageCode
import com.example.flowerworld.ui.components.AppTextButton
import com.example.flowerworld.ui.components.AppTextField

/** A dialog to pick a plant guide entry by name; calls [onPicked] with the chosen one. */
@Composable
fun SpeciesPickerDialog(
    onPicked: (PlantSpecies) -> Unit,
    onDismiss: () -> Unit,
    viewModel: SpeciesSearchViewModel = viewModel(factory = SpeciesSearchViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val language = appLanguageCode()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.picker_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = state.query,
                    onValueChange = viewModel::onQueryChange,
                    label = stringResource(R.string.search_hint),
                    modifier = Modifier.fillMaxWidth()
                )
                val results = state.results
                if (results is GuideResults.Success) {
                    LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                        items(results.species, key = { it.id }) { species ->
                            Text(
                                text = species.displayName(language),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPicked(species) }
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { AppTextButton(text = stringResource(R.string.cancel), onClick = onDismiss) }
    )
}
