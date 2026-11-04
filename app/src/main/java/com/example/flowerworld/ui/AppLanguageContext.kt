package com.example.flowerworld.ui

import android.content.Context
import android.content.res.Configuration
import com.example.flowerworld.data.repository.PrefsSettingsRepository
import java.util.Locale

/**
 * This context with the language the user has chosen in the settings; unchanged until one is chosen.
 * Use it in the activity's attachBaseContext and wherever strings are read outside the UI (notifications).
 */
fun Context.withAppLanguage(): Context {
    val language = PrefsSettingsRepository.storedLanguage(this) ?: return this
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(Locale.forLanguageTag(language.tag))
    return createConfigurationContext(configuration)
}
