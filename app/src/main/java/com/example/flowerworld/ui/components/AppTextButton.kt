package com.example.flowerworld.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.HunterGreen

/** Button without background or border: looks like plain underlined text. */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (enabled) HunterGreen else HunterGreen.copy(alpha = 0.38f),
        textDecoration = TextDecoration.Underline,
        modifier = modifier
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .minimumInteractiveComponentSize()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFDAD7CD)
@Composable
private fun AppTextButtonPreview() {
    FlowerWorldTheme {
        AppTextButton(text = stringResource(R.string.sign_up), onClick = {})
    }
}
