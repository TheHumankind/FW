package com.example.flowerworld.feature.addplant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppButton
import com.example.flowerworld.ui.components.AppPhotoPicker
import com.example.flowerworld.ui.components.AppScreen
import com.example.flowerworld.ui.components.AppTextField
import com.example.flowerworld.ui.components.AppTopBar
import com.example.flowerworld.ui.components.rememberPhotoPicker
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Full screen form that creates a new plant. Calls [onClose] when the user leaves it or the plant is added. */
@Composable
fun AddPlantScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddPlantViewModel = viewModel(factory = AddPlantViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pickPhoto = rememberPhotoPicker(viewModel::onPhotoPicked)

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onClose()
    }

    AddPlantContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onNameChange = viewModel::onNameChange,
        onWateringIntervalChange = viewModel::onWateringIntervalChange,
        onPhotoClick = pickPhoto,
        onAddClick = viewModel::onAddClick,
        onBackClick = onClose,
        modifier = modifier
    )
}

@Composable
private fun AddPlantContent(
    state: AddPlantUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onWateringIntervalChange: (String) -> Unit,
    onPhotoClick: () -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(modifier = modifier.navigationBarsPadding().imePadding()) {
        AppTopBar(title = stringResource(R.string.add_plant), onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppPhotoPicker(photoUri = state.photoUri, onClick = onPhotoClick)
            if (state.saveFailed) {
                Text(
                    text = stringResource(R.string.error_save_failed),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            AppTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = stringResource(R.string.field_title),
                errorText = state.titleError?.message(),
                modifier = Modifier.fillMaxWidth()
            )
            AppTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.field_name),
                errorText = state.nameError?.message(),
                modifier = Modifier.fillMaxWidth()
            )
            AppTextField(
                value = state.wateringInterval,
                onValueChange = onWateringIntervalChange,
                label = stringResource(R.string.field_watering_interval),
                keyboardType = KeyboardType.Number,
                errorText = state.intervalError?.message(),
                modifier = Modifier.fillMaxWidth()
            )
            AppTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.field_description),
                singleLine = false,
                minLines = 3,
                imeAction = ImeAction.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AppButton(
            text = stringResource(R.string.add),
            onClick = onAddClick,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ScreenHorizontalPadding, vertical = 16.dp)
        )
    }
}

@Composable
private fun FieldError.message(): String = when (this) {
    FieldError.Required -> stringResource(R.string.error_required)
    FieldError.InvalidNumber -> stringResource(R.string.error_invalid_number)
}

@Preview(showBackground = true)
@Composable
private fun AddPlantContentPreview() {
    FlowerWorldTheme {
        AddPlantContent(
            state = AddPlantUiState(title = "Monstera", intervalError = FieldError.Required),
            onTitleChange = {},
            onDescriptionChange = {},
            onNameChange = {},
            onWateringIntervalChange = {},
            onPhotoClick = {},
            onAddClick = {},
            onBackClick = {}
        )
    }
}
