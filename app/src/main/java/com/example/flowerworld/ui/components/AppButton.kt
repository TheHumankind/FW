package com.example.flowerworld.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.theme.FlowerWorldTheme
import com.example.flowerworld.ui.theme.HunterGreen
import com.example.flowerworld.ui.theme.Timberwolf

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HunterGreen,
            contentColor = Timberwolf
        )
    ) {
        Text(text = text.uppercase())
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    FlowerWorldTheme {
        AppButton(text = stringResource(R.string.login), onClick = {})
    }
}
