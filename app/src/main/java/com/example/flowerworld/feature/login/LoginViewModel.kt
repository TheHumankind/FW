package com.example.flowerworld.feature.login

import com.example.flowerworld.ui.UiStateViewModel

data class LoginUiState(
    val login: String = "",
    val password: String = ""
)

class LoginViewModel : UiStateViewModel<LoginUiState>(LoginUiState()) {
    fun onLoginChange(value: String) = updateState { copy(login = value) }

    fun onPasswordChange(value: String) = updateState { copy(password = value) }
}
