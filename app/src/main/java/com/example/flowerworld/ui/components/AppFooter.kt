package com.example.flowerworld.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme

/** Sections the footer switches between, in display order. */
enum class FooterTab(val icon: ImageVector, @StringRes val label: Int) {
    Plants(Icons.Filled.Eco, R.string.footer_plants),
    Profile(Icons.Filled.AccountCircle, R.string.footer_profile)
}

private val ButtonSize = 38.dp
private val SelectedButtonSize = ButtonSize + 12.dp

/**
 * Bottom bar of the screen: fills the width and extends under the system navigation.
 * State-switching icon buttons are centered; the selected one is slightly larger.
 */
@Composable
fun AppFooter(
    selectedTab: FooterTab,
    onSelect: (FooterTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            // fixed content height: the bar does not resize when buttons switch
            .height(SelectedButtonSize),
        horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FooterTab.entries.forEach { tab ->
            val size by animateDpAsState(
                targetValue = if (tab == selectedTab) SelectedButtonSize else ButtonSize,
                label = "footerButtonSize"
            )
            AppIconButton(
                icon = tab.icon,
                contentDescription = stringResource(tab.label),
                tint = if (tab == selectedTab) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                },
                size = size,
                onClick = { onSelect(tab) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppFooterPreview() {
    FlowerWorldTheme {
        AppFooter(selectedTab = FooterTab.Plants, onSelect = {})
    }
}
