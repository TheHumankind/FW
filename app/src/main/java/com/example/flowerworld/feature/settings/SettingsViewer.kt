package com.example.flowerworld.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.components.AppListScreen
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** App settings. Placeholder for now: a header and a text. */
@Composable
fun SettingsViewer(modifier: Modifier = Modifier) {
    AppListScreen(title = stringResource(R.string.settings_header), modifier = modifier) {
        AppCenteredMessage(text = stringResource(R.string.settings_placeholder))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsViewerPreview() {
    FlowerWorldTheme {
        SettingsViewer()
    }
}
