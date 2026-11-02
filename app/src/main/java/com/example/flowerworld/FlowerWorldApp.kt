package com.example.flowerworld

import android.app.Application
import com.example.flowerworld.di.AppContainer

class FlowerWorldApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
