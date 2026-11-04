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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.model.PlantPhoto
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppIconButton
import com.example.flowerworld.ui.components.AppScreen
import com.example.flowerworld.ui.components.LongPressMenuBox
import com.example.flowerworld.ui.components.MenuCardItem
import com.example.flowerworld.ui.components.AppTextButton
import com.example.flowerworld.ui.components.PhotoPager
import com.example.flowerworld.ui.components.photoPageCount
import com.example.flowerworld.ui.components.rememberPhotoPicker
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
    val pickPhoto = rememberPhotoPicker(viewModel::onPhotoPicked)
    PlantInfoContent(
        state = state,
        onAddPhotoClick = pickPhoto,
        onDeletePhoto = viewModel::onPhotoDelete,
        onBackClick = onBackClick,
        onEditRequest = viewModel::onEditRequest,
        onEditInput = viewModel::onEditInput,
        onEditDismiss = viewModel::onEditDismiss,
        onEditSave = viewModel::onEditSave,
        modifier = modifier
    )
}

@Composable
private fun PlantInfoContent(
    state: PlantInfoUiState,
    onAddPhotoClick: () -> Unit,
    onDeletePhoto: (PlantPhoto) -> Unit,
    onBackClick: () -> Unit,
    onEditRequest: (PlantField) -> Unit,
    onEditInput: () -> Unit,
    onEditDismiss: () -> Unit,
    onEditSave: (text: String, unit: IntervalUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                PlantInfoUiState.Loading -> Unit
                PlantInfoUiState.Error -> AppCenteredMessage(text = stringResource(R.string.plant_not_found))
                is PlantInfoUiState.Success -> {
                    PlantDetails(state.plant, state.photoFailed, onAddPhotoClick, onDeletePhoto, onEditRequest)
                    state.edit?.let { edit ->
                        EditPlantFieldDialog(
                            plant = state.plant,
                            edit = edit,
                            onSave = onEditSave,
                            onInput = onEditInput,
                            onDismiss = onEditDismiss
                        )
                    }
                }
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
private fun PlantDetails(
    plant: Plant,
    photoFailed: Boolean,
    onAddPhotoClick: () -> Unit,
    onDeletePhoto: (PlantPhoto) -> Unit,
    onEditRequest: (PlantField) -> Unit
) {
    val photos = plant.photosNewestFirst
    val pagerState = rememberPagerState { photoPageCount(photos.size) }
    // a new photo is the newest one: show it
    var lastPhotoCount by remember { mutableIntStateOf(photos.size) }
    LaunchedEffect(photos.size) {
        if (photos.size > lastPhotoCount) pagerState.animateScrollToPage(0)
        lastPhotoCount = photos.size
    }
    var photoToDelete by remember { mutableStateOf<PlantPhoto?>(null) }
    photoToDelete?.let { photo ->
        DeletePhotoDialog(
            onConfirm = {
                photoToDelete = null
                onDeletePhoto(photo)
            },
            onDismiss = { photoToDelete = null }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // a long press on the photos offers to add a photo or to delete the one on screen
        LongPressMenuBox(
            menu = { dismiss ->
                MenuCardItem(
                    text = stringResource(R.string.add_photo),
                    onClick = {
                        dismiss()
                        onAddPhotoClick()
                    }
                )
                photos.getOrNull(pagerState.currentPage)?.let { photo ->
                    MenuCardItem(
                        text = stringResource(R.string.delete_photo),
                        color = MaterialTheme.colorScheme.error,
                        onClick = {
                            dismiss()
                            photoToDelete = photo
                        }
                    )
                }
            }
        ) {
            PhotoPager(
                photoPaths = photos.map { it.path },
                pagerState = pagerState,
                contentDescription = plant.name,
                placeholder = Icons.Filled.Eco,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PhotoHeight)
            )
        }
        if (photoFailed) {
            Text(
                text = stringResource(R.string.error_save_failed),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = Dimens.ScreenHorizontalPadding)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenHorizontalPadding)
                // leave room for the back button that floats over the bottom of the list
                .navigationBarsPadding()
                .padding(bottom = BackButtonSize + BackButtonPadding * 2),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // a long press on an editable row offers to edit it; the system rows below cannot be edited
            EditableInfoRow(stringResource(R.string.field_name), plant.name) { onEditRequest(PlantField.Name) }
            EditableInfoRow(stringResource(R.string.field_title), plant.title) { onEditRequest(PlantField.Title) }
            // an empty note still has a row, otherwise there would be nothing to long press to add one
            EditableInfoRow(stringResource(R.string.field_notes), plant.notes.ifBlank { EmptyValue }) {
                onEditRequest(PlantField.Notes)
            }
            EditableInfoRow(
                stringResource(R.string.field_watering_interval),
                wateringIntervalText(plant.wateringIntervalHours)
            ) { onEditRequest(PlantField.WateringInterval) }
            InfoRow(stringResource(R.string.info_last_watered), formatDateTime(plant.lastWateredAt))
            // a dead plant has no next watering, it has a date of death instead
            plant.diedAt
                ?.let { InfoRow(stringResource(R.string.info_died), formatDateTime(it)) }
                ?: InfoRow(stringResource(R.string.info_next_watering), formatDateTime(plant.nextWateringAt))
        }
    }
}

@Composable
private fun DeletePhotoDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_photo_title)) },
        text = { Text(stringResource(R.string.delete_photo_text)) },
        confirmButton = { AppTextButton(text = stringResource(R.string.delete), onClick = onConfirm) },
        dismissButton = { AppTextButton(text = stringResource(R.string.cancel), onClick = onDismiss) }
    )
}

/** An [InfoRow] whose long press opens a small menu with "Edit"; [onEditClick] opens the editor. */
@Composable
private fun EditableInfoRow(label: String, value: String, onEditClick: () -> Unit) {
    LongPressMenuBox(
        menu = { dismiss ->
            MenuCardItem(
                text = stringResource(R.string.edit),
                onClick = {
                    dismiss()
                    onEditClick()
                }
            )
        }
    ) {
        InfoRow(label, value)
    }
}

private const val EmptyValue = "\u2014"

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
            onAddPhotoClick = {},
            onDeletePhoto = {},
            onBackClick = {},
            onEditRequest = {},
            onEditInput = {},
            onEditDismiss = {},
            onEditSave = { _, _ -> }
        )
    }
}

/** The interval in whole days when it is a multiple of 24 hours, otherwise in hours. */
@Composable
private fun wateringIntervalText(hours: Int): String =
    if (hours % HoursInDay == 0) {
        pluralStringResource(R.plurals.days_count, hours / HoursInDay, hours / HoursInDay)
    } else {
        pluralStringResource(R.plurals.hours_count, hours, hours)
    }

private const val HoursInDay = 24
