package com.example.flowerworld.domain.repository

import com.example.flowerworld.domain.model.AppLanguage
import com.example.flowerworld.domain.model.IntervalUnit
import com.example.flowerworld.domain.model.PlantHeading
import kotlinx.coroutines.flow.Flow

/** User-level settings and one-time flags of the app. */
interface SettingsRepository {
    /** True once the user has seen the hint about battery optimization and autostart. */
    val reminderHintShown: Flow<Boolean>

    /** The language of the app: the user's choice, or the device language until one is chosen. */
    val language: Flow<AppLanguage>

    /** What the time left until the next watering is counted in on a plant card. */
    val cardTimeUnit: Flow<IntervalUnit>

    /** Which name of a plant is its heading on the cards. */
    val cardHeading: Flow<PlantHeading>

    suspend fun markReminderHintShown()

    suspend fun setLanguage(language: AppLanguage)

    suspend fun setCardTimeUnit(unit: IntervalUnit)

    suspend fun setCardHeading(heading: PlantHeading)
}
