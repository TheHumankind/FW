package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

/** User-level settings and one-time flags of the app. */
interface SettingsRepository {
    /** True once the user has seen the hint about battery optimization and autostart. */
    val reminderHintShown: Flow<Boolean>

    /** The language of the app: the user's choice, or the device language until one is chosen. */
    val language: Flow<AppLanguage>

    suspend fun markReminderHintShown()

    suspend fun setLanguage(language: AppLanguage)
}
