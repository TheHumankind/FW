package com.example.flowerworld

import android.app.Application
import com.example.flowerworld.di.AppContainer
import com.example.flowerworld.reminder.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FlowerWorldApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        ReminderReceiver.createChannel(this)
        // keep the watering alarms in step with the database (new plants, waterings)
        appScope.launch {
            container.plantRepository.plants.collect { container.reminderScheduler.sync(it) }
        }
    }
}
