package com.example.flowerworld.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.flowerworld.feature.login.LoginScreen
import com.example.flowerworld.feature.main.MainScreen
import com.example.flowerworld.feature.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Main }

// Simple state-based navigation; replace with Navigation Compose when there are more screens.
@Composable
fun AppNavigation() {
    var screen by rememberSaveable { mutableStateOf(Screen.Welcome) }

    when (screen) {
        Screen.Welcome -> WelcomeScreen(onLoginClick = { screen = Screen.Login })
        Screen.Login -> {
            BackHandler { screen = Screen.Welcome }
            // No validation yet: "Confirm" goes straight to the main screen
            LoginScreen(onConfirmClick = { screen = Screen.Main })
        }
        Screen.Main -> MainScreen()
    }
}
