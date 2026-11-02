package com.example.flowerworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.flowerworld.feature.login.LoginScreen
import com.example.flowerworld.ui.theme.FlowerWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowerWorldTheme {
                LoginScreen()
            }
        }
    }
}
