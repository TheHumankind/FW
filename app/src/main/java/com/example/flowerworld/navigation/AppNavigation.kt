package com.example.flowerworld.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flowerworld.feature.addplant.AddPlantScreen
import com.example.flowerworld.feature.addplant.AddPlantViewModel
import com.example.flowerworld.feature.guide.SpeciesDetailScreen
import com.example.flowerworld.feature.guide.SpeciesDetailViewModel
import com.example.flowerworld.feature.login.LoginScreen
import com.example.flowerworld.feature.main.MainScreen
import com.example.flowerworld.feature.plantinfo.PlantInfoScreen
import com.example.flowerworld.feature.plantinfo.PlantInfoViewModel
import com.example.flowerworld.feature.welcome.WelcomeScreen

/** Routes of the app; each one has its own back stack entry and so its own ViewModels. */
private object Routes {
    const val Welcome = "welcome"
    const val Login = "login"
    const val Main = "main"
    const val AddPlant = "add_plant?title={${AddPlantViewModel.TitleArg}}&interval={${AddPlantViewModel.IntervalArg}}&species={${AddPlantViewModel.SpeciesArg}}"
    const val Species = "species/{${SpeciesDetailViewModel.SpeciesIdArg}}"
    const val PlantInfo = "plant_info/{${PlantInfoViewModel.PlantIdArg}}"

    fun plantInfo(plantId: String) = "plant_info/$plantId"

    fun species(id: String) = "species/$id"

    /** The add-plant form, optionally with the title and watering interval (days) filled in. */
    fun addPlant(title: String? = null, intervalDays: Int? = null, speciesId: String? = null) =
        "add_plant?title=${Uri.encode(title.orEmpty())}&interval=${intervalDays ?: 0}&species=${Uri.encode(speciesId.orEmpty())}"
}

// TODO: switch back to Routes.Welcome once login and sign up are really used
private const val StartDestination = Routes.Main

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = StartDestination) {
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
            MainScreen(
                onAddPlantClick = { navController.navigate(Routes.addPlant()) },
                onSpeciesClick = { species -> navController.navigate(Routes.species(species.id)) },
                onPlantClick = { plant -> navController.navigate(Routes.plantInfo(plant.id)) }
            )
        }
        composable(
            route = Routes.PlantInfo,
            arguments = listOf(navArgument(PlantInfoViewModel.PlantIdArg) { type = NavType.StringType })
        ) {
            PlantInfoScreen(onBackClick = { navController.popBackStack() })
        }
        composable(
            route = Routes.Species,
            arguments = listOf(navArgument(SpeciesDetailViewModel.SpeciesIdArg) { type = NavType.StringType })
        ) {
            SpeciesDetailScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { title, days, speciesId -> navController.navigate(Routes.addPlant(title, days, speciesId)) }
            )
        }
        composable(
            route = Routes.AddPlant,
            arguments = listOf(
                navArgument(AddPlantViewModel.TitleArg) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(AddPlantViewModel.IntervalArg) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(AddPlantViewModel.SpeciesArg) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            AddPlantScreen(onClose = { navController.popBackStack() })
        }
    }
}
