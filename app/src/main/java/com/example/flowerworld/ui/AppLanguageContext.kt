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

/** The configuration of this context with [language] as its locale. */
private fun Context.configurationFor(language: AppLanguage): Configuration =
    Configuration(resources.configuration).apply { setLocale(Locale.forLanguageTag(language.tag)) }

/** [resources] of this context in [language]. */
private fun Context.localizedResources(language: AppLanguage): Resources =
    createConfigurationContext(configurationFor(language)).resources

/**
 * This context with the language the user has chosen in the settings; unchanged until one is chosen.
 * Use it in the activity's attachBaseContext and wherever strings are read outside the UI (notifications).
 */
fun Context.withAppLanguage(): Context {
    val language = PrefsSettingsRepository.storedLanguage(this) ?: return this
    return createConfigurationContext(configurationFor(language))
}

/** The two-letter language code of the app (it follows the language chosen in the settings). */
@Composable
fun appLanguageCode(): String = LocalConfiguration.current.locales[0].language

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
