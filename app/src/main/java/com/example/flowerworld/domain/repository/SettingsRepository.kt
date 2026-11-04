package com.example.flowerworld.domain.repository

import kotlinx.coroutines.flow.Flow

/** User-level settings and one-time flags of the app. */
interface SettingsRepository {
    /** True once the user has seen the hint about battery optimization and autostart. */
    val reminderHintShown: Flow<Boolean>

    suspend fun markReminderHintShown()
}
