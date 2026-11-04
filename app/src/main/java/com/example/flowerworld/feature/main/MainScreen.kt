package com.example.flowerworld.feature.main

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.data.fake.TestPlants
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.feature.account.AccountViewer
import com.example.flowerworld.feature.plants.PlantViewer
import com.example.flowerworld.ui.components.AppFooter
import com.example.flowerworld.ui.components.AppScreen
import com.example.flowerworld.ui.components.FooterTab
import com.example.flowerworld.ui.theme.FlowerWorldTheme

@Composable
fun MainScreen(
    onAddPlantClick: () -> Unit,
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MainContent(
        state = state,
        onFooterSelect = viewModel::onFooterSelect,
        onAddPlantClick = onAddPlantClick,
        onPlantClick = onPlantClick,
        modifier = modifier
    )
}

@Composable
private fun MainContent(
    state: MainUiState,
    onFooterSelect: (FooterTab) -> Unit,
    onAddPlantClick: () -> Unit,
    onPlantClick: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(modifier = modifier.statusBarsPadding()) {
        when (state.selectedTab) {
            FooterTab.Plants -> PlantViewer(
                plants = state.plants,
                onAddPlantClick = onAddPlantClick,
                onPlantClick = onPlantClick,
                modifier = Modifier.weight(1f)
            )
            FooterTab.Profile -> AccountViewer(Modifier.weight(1f))
        }
        AppFooter(
            selectedTab = state.selectedTab,
            onSelect = onFooterSelect
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
    FlowerWorldTheme {
        MainContent(
            state = MainUiState(plants = TestPlants),
            onFooterSelect = {},
            onAddPlantClick = {},
            onPlantClick = {}
        )
    }
}
