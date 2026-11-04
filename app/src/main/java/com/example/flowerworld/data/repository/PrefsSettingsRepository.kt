package com.example.flowerworld.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.flowerworld.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Keeps settings in SharedPreferences. Good enough for a couple of flags; move to DataStore
 * when real settings (theme, language) arrive.
 */
class PrefsSettingsRepository(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val hintShown = MutableStateFlow(prefs.getBoolean(KeyReminderHintShown, false))

    override val reminderHintShown: Flow<Boolean> = hintShown.asStateFlow()

    override suspend fun markReminderHintShown() {
        prefs.edit { putBoolean(KeyReminderHintShown, true) }
        hintShown.value = true
    }

    private companion object {
        const val PrefsName = "settings"
        const val KeyReminderHintShown = "reminderHintShown"
    }
}
