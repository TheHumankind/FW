package com.example.flowerworld.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.ui.theme.Dimens
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Content of a footer tab: the [title] in capitals on top and the rest of the space for [content]. */
@Composable
fun AppListScreen(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.ListPadding, end = Dimens.ListPadding, top = Dimens.ListPadding)
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), content = content)
    }
}

@Preview(showBackground = true)
@Composable
private fun AppListScreenPreview() {
    FlowerWorldTheme {
        AppListScreen(title = "Your plants") {
            AppCenteredMessage(text = "Nothing here yet")
        }
    }
}
