package com.example.flowerworld.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.PlantHeading
import com.example.flowerworld.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime

/**
 * Keeps settings in SharedPreferences. Good enough for a couple of flags; move to DataStore
 * when more settings (theme) arrive.
 */
class PrefsSettingsRepository(context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val hintShown = MutableStateFlow(prefs.getBoolean(KeyReminderHintShown, false))
    private val languageState = MutableStateFlow(storedLanguage(context) ?: AppLanguage.systemDefault())

    private val cardTimeUnitState = MutableStateFlow(
        IntervalUnit.entries.firstOrNull { it.name == prefs.getString(KeyCardTimeUnit, null) } ?: IntervalUnit.Days
    )

    private val cardHeadingState = MutableStateFlow(
        PlantHeading.entries.firstOrNull { it.name == prefs.getString(KeyCardHeading, null) } ?: PlantHeading.Title
    )

    private val reminderTimeState = MutableStateFlow(
        LocalTime.ofSecondOfDay(prefs.getInt(KeyReminderMinutes, DefaultReminderMinutes) * 60L)
    )

    override val reminderTime: Flow<LocalTime> = reminderTimeState.asStateFlow()

    override val reminderHintShown: Flow<Boolean> = hintShown.asStateFlow()

    override val language: Flow<AppLanguage> = languageState.asStateFlow()

    override val cardTimeUnit: Flow<IntervalUnit> = cardTimeUnitState.asStateFlow()

    override val cardHeading: Flow<PlantHeading> = cardHeadingState.asStateFlow()

    override suspend fun markReminderHintShown() {
        prefs.edit { putBoolean(KeyReminderHintShown, true) }
        hintShown.value = true
    }

    override suspend fun setLanguage(language: AppLanguage) {
        prefs.edit { putString(KeyLanguage, language.tag) }
        languageState.value = language
    }

    override suspend fun setCardTimeUnit(unit: IntervalUnit) {
        prefs.edit { putString(KeyCardTimeUnit, unit.name) }
        cardTimeUnitState.value = unit
    }

    override suspend fun setCardHeading(heading: PlantHeading) {
        prefs.edit { putString(KeyCardHeading, heading.name) }
        cardHeadingState.value = heading
    }

    override suspend fun setReminderTime(time: LocalTime) {
        val minutes = time.hour * 60 + time.minute
        prefs.edit { putInt(KeyReminderMinutes, minutes) }
        reminderTimeState.value = LocalTime.of(time.hour, time.minute)
    }

    companion object {
        /** The daily notification is shown at 09:00 until the user chooses another time. */
        private const val DefaultReminderMinutes = 9 * 60
        private const val KeyReminderMinutes = "reminderMinutes"
        private const val PrefsName = "settings"
        private const val KeyReminderHintShown = "reminderHintShown"
        private const val KeyLanguage = "language"
        private const val KeyCardTimeUnit = "cardTimeUnit"
        private const val KeyCardHeading = "cardHeading"

        /**
         * The language the user has chosen, or null while the app follows the device language.
         * Static because the language must be known before the app container exists (attachBaseContext).
         */
        fun storedLanguage(context: Context): AppLanguage? = AppLanguage.fromTag(
            context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE).getString(KeyLanguage, null)
        )
    }
}
