package com.example.flowerworld.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.ui.components.AppDropdown
import com.example.flowerworld.ui.components.AppListScreen
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** App settings: the language of the app. */
@Composable
fun SettingsViewer(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    SettingsContent(
        state = state,
        // the activity restarts to read all strings in the new language
        onLanguageSelect = { language -> viewModel.onLanguageSelect(language) { activity?.recreate() } },
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onLanguageSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    AppListScreen(title = stringResource(R.string.settings_header), modifier = modifier) {
        if (state is SettingsUiState.Success) {
            Column(modifier = Modifier.fillMaxWidth().padding(Dimens.ListPadding)) {
                AppDropdown(
                    items = AppLanguage.entries,
                    selected = state.language,
                    onSelect = onLanguageSelect,
                    label = stringResource(R.string.settings_language),
                    itemLabel = { stringResource(it.nameRes) },
                    itemLeading = { Text(it.flag) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    FlowerWorldTheme {
        SettingsContent(state = SettingsUiState.Success(AppLanguage.English), onLanguageSelect = {})
    }
}
