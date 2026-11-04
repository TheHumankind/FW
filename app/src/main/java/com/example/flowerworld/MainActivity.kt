package com.example.flowerworld

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.navigation.AppNavigation
import com.example.flowerworld.ui.AppLanguageProvider
import com.example.flowerworld.ui.withAppLanguage
import com.example.flowerworld.ui.theme.FlowerWorldTheme

class MainActivity : ComponentActivity() {
    private val container get() = (application as FlowerWorldApp).container

    /** Applies the language chosen in the settings (the device language until one is chosen). */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withAppLanguage())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            val language by container.settingsRepository.language
                .collectAsStateWithLifecycle(AppLanguage.systemDefault())
            // the language is switched in place, without restarting the activity
            AppLanguageProvider(language) {
                FlowerWorldTheme {
                    AppNavigation()
                }
            }
        }
    }

    /** Android 13+ needs the user's consent to show the watering reminders. */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            .launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
