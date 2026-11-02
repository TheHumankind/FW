package com.example.flowerworld.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flowerworld.FlowerWorldApp

/** The [AppContainer] of the application this ViewModel is created in. */
val CreationExtras.appContainer: AppContainer
    get() = (this[APPLICATION_KEY] as FlowerWorldApp).container

/** Builds a ViewModel factory whose [create] receives the [AppContainer]. */
inline fun <reified VM : ViewModel> containerViewModelFactory(
    crossinline create: (AppContainer) -> VM
): ViewModelProvider.Factory = viewModelFactory {
    initializer { create(appContainer) }
}
