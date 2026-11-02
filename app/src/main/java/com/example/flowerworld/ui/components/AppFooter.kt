package com.example.flowerworld.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.ui.theme.Brown
import com.example.flowerworld.ui.theme.DebugRed
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.Sage

// Temporary debug colors for the state-switching buttons
private val FooterButtonColors: List<Color> = listOf(DebugRed, Brown)

private val ButtonSize = 40.dp
private val SelectedButtonSize = ButtonSize + 8.dp

/**
 * Bottom bar of the screen: fills the width and extends under the system navigation.
 * State-switching buttons are centered; the selected one is slightly larger.
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
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FooterButtonColors.forEachIndexed { index, color ->
            val size by animateDpAsState(
                targetValue = if (index == selectedIndex) SelectedButtonSize else ButtonSize,
                label = "footerButtonSize"
            )
            AppRoundButton(color = color, size = size, onClick = { onSelect(index) })
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
