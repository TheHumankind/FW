package com.example.flowerworld.feature.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppCenteredMessage
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Shows the user's account. Placeholder for now: only a text. */
@Composable
fun AccountViewer(modifier: Modifier = Modifier) {
    AppCenteredMessage(
        text = stringResource(R.string.account_viewer_placeholder).uppercase(),
        bold = true,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountViewerPreview() {
    FlowerWorldTheme {
        AccountViewer()
    }
}
