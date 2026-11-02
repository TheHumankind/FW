package com.example.flowerworld.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowerworld.R
import com.example.flowerworld.ui.components.AppButton
import com.example.flowerworld.ui.components.AppPasswordField
import com.example.flowerworld.ui.components.AppTextField
import com.example.flowerworld.ui.theme.FlowerWorldTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onConfirmClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContent(
        state = state,
        onLoginChange = viewModel::onLoginChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmClick = onConfirmClick,
        modifier = modifier
    )
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppTextField(
            value = state.login,
            onValueChange = onLoginChange,
            label = stringResource(R.string.login_label),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        AppPasswordField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password_label),
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Done
        )
        Spacer(Modifier.height(24.dp))
        AppButton(
            text = stringResource(R.string.confirm),
            onClick = onConfirmClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    FlowerWorldTheme {
        LoginContent(
            state = LoginUiState(),
            onLoginChange = {},
            onPasswordChange = {},
            onConfirmClick = {}
        )
    }
}
