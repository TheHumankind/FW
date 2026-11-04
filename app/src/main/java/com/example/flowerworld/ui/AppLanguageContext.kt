package com.example.flowerworld.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.example.flowerworld.data.repository.PrefsSettingsRepository
import com.example.flowerworld.domain.model.AppLanguage
import java.util.Locale

/** [resources] of this context in [language]. */
private fun Context.localizedResources(language: AppLanguage): Resources {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(Locale.forLanguageTag(language.tag))
    return createConfigurationContext(configuration).resources
}

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

/**
 * Shows [content] in [language] and switches it in place when [language] changes: no activity restart,
 * so the screen does not flicker and keeps its state. The context stays the activity's own
 * (everything but the resources is delegated to it).
 */
@Composable
fun AppLanguageProvider(language: AppLanguage, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val localizedContext = remember(context, language) {
        val resources = context.localizedResources(language)
        object : ContextWrapper(context) {
            override fun getResources(): Resources = resources
        }
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalResources provides localizedContext.resources,
        LocalConfiguration provides localizedContext.resources.configuration,
        content = content
    )
}
