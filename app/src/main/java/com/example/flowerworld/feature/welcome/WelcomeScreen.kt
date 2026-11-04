package com.example.flowerworld.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppButton
import com.example.flowerworld.ui.components.AppCenteredScreen
import com.example.flowerworld.ui.components.AppTextButton
import com.example.flowerworld.ui.theme.FlowerWorldTheme

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    AppCenteredScreen(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.logo_description),
            modifier = Modifier.size(160.dp)
        )
        Spacer(Modifier.height(35.dp))
        AppButton(
            text = stringResource(R.string.login),
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(15.dp))
        AppTextButton(
            text = stringResource(R.string.sign_up),
            onClick = onSignUpClick,
            // offset the invisible 48dp touch-target padding so the visible gap is 15dp
            modifier = Modifier.offset(y = (-16).dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    FlowerWorldTheme {
        WelcomeScreen()
    }
}
