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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.BrunswickGreen
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.HunterGreen
import com.example.flowerworld.ui.theme.Sage

private data class FooterItem(val icon: ImageVector, @StringRes val label: Int)

private val FooterItems = listOf(
    FooterItem(Icons.Filled.Eco, R.string.footer_plants),
    FooterItem(Icons.Filled.AccountCircle, R.string.footer_profile)
)

private val ButtonSize = 38.dp
private val SelectedButtonSize = ButtonSize + 12.dp

/**
 * Bottom bar of the screen: fills the width and extends under the system navigation.
 * State-switching icon buttons are centered; the selected one is slightly larger.
 */
@Composable
fun AppFooter(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Sage)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            // fixed content height: the bar does not resize when buttons switch
            .height(SelectedButtonSize),
        horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FooterItems.forEachIndexed { index, item ->
            val size by animateDpAsState(
                targetValue = if (index == selectedIndex) SelectedButtonSize else ButtonSize,
                label = "footerButtonSize"
            )
            AppIconButton(
                icon = item.icon,
                contentDescription = stringResource(item.label),
                tint = if (index == selectedIndex) BrunswickGreen else HunterGreen,
                size = size,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppFooterPreview() {
    FlowerWorldTheme {
        AppFooter(selectedIndex = 0, onSelect = {})
    }
}
