package com.example.flowerworld.reminder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.flowerworld.FlowerWorldApp
import com.example.flowerworld.MainActivity
import com.example.flowerworld.R
import com.example.flowerworld.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Runs [block] in the app container off the main thread, keeping the receiver alive until it ends. */
internal fun BroadcastReceiver.launchAsync(context: Context, block: suspend (AppContainer) -> Unit) {
    val pending = goAsync()
    val container = (context.applicationContext as FlowerWorldApp).container
    CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
        try {
            block(container)
        } finally {
            pending.finish()
        }
    }
}

/** Fires when a plant needs watering: shows the notification and plans a repeat in case it is ignored. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ActionRemind) return
        val plantId = intent.getStringExtra(ExtraPlantId) ?: return
        launchAsync(context) { container ->
            val plant = container.plantRepository.plants.first().firstOrNull { it.id == plantId }
                ?: return@launchAsync
            // reminded again later until the user taps "Watered"
            container.reminderScheduler.scheduleRepeat(plantId)
            showNotification(context, plantId, plant.name)
        }
    }

    private fun showNotification(context: Context, plantId: String, plantName: String) {
        val canNotify = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        if (!canNotify) return
        createChannel(context)

        val requestCode = plantIntId(plantId)
        val open = PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val watered = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, WateredReceiver::class.java)
                .setAction(WateredReceiver.ActionWatered)
                .putExtra(ExtraPlantId, plantId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification: Notification = NotificationCompat.Builder(context, ChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(plantName)
            .setContentText(context.getString(R.string.reminder_text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(0, context.getString(R.string.action_watered), watered)
            .build()
        NotificationManagerCompat.from(context).notify(plantIntId(plantId), notification)
    }

    companion object {
        const val ActionRemind = "com.example.flowerworld.action.REMIND"
        const val ExtraPlantId = "plantId"
        const val ChannelId = "watering"

        /** Int derived from the plant id: the notification id and the request code of its PendingIntents. */
        fun plantIntId(plantId: String): Int = plantId.hashCode()

        /** Creates the notification channel; safe to call many times. */
        fun createChannel(context: Context) {
            val channel = NotificationChannel(
                ChannelId,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = context.getString(R.string.notification_channel_description) }
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }
}

/** The "Watered" button of the notification: records the watering and plans the next reminder. */
class WateredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ActionWatered) return
        val plantId = intent.getStringExtra(ReminderReceiver.ExtraPlantId) ?: return
        NotificationManagerCompat.from(context).cancel(ReminderReceiver.plantIntId(plantId))
        launchAsync(context) { container ->
            container.plantRepository.markWatered(plantId)
            container.reminderScheduler.sync(container.plantRepository.plants.first())
        }
    }

    companion object {
        const val ActionWatered = "com.example.flowerworld.action.WATERED"
    }
}

/** Alarms are lost on reboot: sets them again. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        launchAsync(context) { container ->
            container.reminderScheduler.sync(container.plantRepository.plants.first())
        }
    }
}
