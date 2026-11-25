package com.example.flowerworld

import android.app.Application
import com.example.flowerworld.di.AppContainer
import com.example.flowerworld.reminder.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FlowerWorldApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        ReminderReceiver.createChannel(this)
        // keep the watering alarms in step with the database (new plants, waterings)
        appScope.launch {
            // also re-planned when the user changes the notification time
            combine(container.plantRepository.plants, container.settingsRepository.reminderTime) { plants, _ -> plants }
                .collect { container.reminderScheduler.sync(it) }
        }
    }
}
