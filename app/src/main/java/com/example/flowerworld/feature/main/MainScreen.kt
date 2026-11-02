package com.example.flowerworld.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.ui.components.AppFooter
import com.example.flowerworld.ui.theme.FlowerWorldTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MainContent(
        state = state,
        onFooterSelect = viewModel::onFooterSelect,
        modifier = modifier
    )
}

@Composable
private fun MainContent(
    state: MainUiState,
    onFooterSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.weight(1f))
        AppFooter(
            selectedIndex = state.selectedIndex,
            onSelect = onFooterSelect
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
    FlowerWorldTheme {
        MainContent(state = MainUiState(), onFooterSelect = {})
    }
}
