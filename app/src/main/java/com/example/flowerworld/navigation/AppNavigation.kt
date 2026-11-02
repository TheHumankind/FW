package com.example.flowerworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flowerworld.feature.addplant.AddPlantScreen
import com.example.flowerworld.feature.login.LoginScreen
import com.example.flowerworld.feature.main.MainScreen
import com.example.flowerworld.feature.welcome.WelcomeScreen

/** Routes of the app; each one has its own back stack entry and so its own ViewModels. */
private object Routes {
    const val Welcome = "welcome"
    const val Login = "login"
    const val Main = "main"
    const val AddPlant = "add_plant"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Welcome) {
        composable(Routes.Welcome) {
            WelcomeScreen(onLoginClick = { navController.navigate(Routes.Login) })
        }
        composable(Routes.Login) {
            // No validation yet: "Confirm" goes straight to the main screen
            LoginScreen(
                onConfirmClick = {
                    navController.navigate(Routes.Main) {
                        // after logging in, "back" leaves the app instead of returning to the welcome screen
                        popUpTo(Routes.Welcome) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Main) {
            MainScreen(onAddPlantClick = { navController.navigate(Routes.AddPlant) })
        }
        composable(Routes.AddPlant) {
            AddPlantScreen(onClose = { navController.popBackStack() })
        }
    }
}
