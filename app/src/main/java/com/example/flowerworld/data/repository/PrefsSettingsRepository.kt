package com.example.flowerworld.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Keeps settings in SharedPreferences. Good enough for a couple of flags; move to DataStore
 * when more settings (theme) arrive.
 */
class PrefsSettingsRepository(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val hintShown = MutableStateFlow(prefs.getBoolean(KeyReminderHintShown, false))
    private val languageState = MutableStateFlow(storedLanguage(context) ?: AppLanguage.systemDefault())

    override val reminderHintShown: Flow<Boolean> = hintShown.asStateFlow()

    override val language: Flow<AppLanguage> = languageState.asStateFlow()

    override suspend fun markReminderHintShown() {
        prefs.edit { putBoolean(KeyReminderHintShown, true) }
        hintShown.value = true
    }

    override suspend fun setLanguage(language: AppLanguage) {
        prefs.edit { putString(KeyLanguage, language.tag) }
        languageState.value = language
    }

    companion object {
        private const val PrefsName = "settings"
        private const val KeyReminderHintShown = "reminderHintShown"
        private const val KeyLanguage = "language"

        /**
         * The language the user has chosen, or null while the app follows the device language.
         * Static because the language must be known before the app container exists (attachBaseContext).
         */
        fun storedLanguage(context: Context): AppLanguage? = AppLanguage.fromTag(
            context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE).getString(KeyLanguage, null)
        )
    }
}
