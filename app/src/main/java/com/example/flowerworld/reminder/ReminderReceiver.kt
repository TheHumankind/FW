package com.example.flowerworld.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.flowerworld.FlowerWorldApp
import com.example.flowerworld.MainActivity
import com.example.flowerworld.R
import com.example.flowerworld.di.AppContainer
import com.example.flowerworld.ui.withAppLanguage
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

/** Fires once a day: shows how many plants are to be watered today and how many are overdue. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // alarms of the old per-plant reminders carry another action and are ignored
        if (intent.action != ActionDaily) return
        launchAsync(context) { container ->
            // the next day's summary is planned first, so a failure below cannot break the chain
            container.reminderScheduler.scheduleNextDaily()
            val plants = container.plantRepository.plants.first()
            val summary = WateringSummary.of(plants, container.reminderScheduler.nowMillis())
            if (summary.isEmpty) {
                NotificationManagerCompat.from(context).cancel(SummaryNotificationId)
            } else {
                showNotification(context, summary)
            }
        }
    }

    private fun showNotification(context: Context, summary: WateringSummary) {
        // false when the user denied the notification permission (Android 13+) or turned the app's notifications off
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        createChannel(context)
        // strings in the language chosen in the settings, not the device one
        val localized = context.withAppLanguage()

        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val lines = buildList {
            if (summary.dueToday > 0) add(localized.getString(R.string.reminder_due_today, summary.dueToday))
            if (summary.overdue > 0) add(localized.getString(R.string.reminder_overdue, summary.overdue))
        }
        val notification: Notification = NotificationCompat.Builder(context, ChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(localized.getString(R.string.reminder_title))
            .setContentText(lines.joinToString(", "))
            .setStyle(NotificationCompat.BigTextStyle().bigText(lines.joinToString("\n")))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(SummaryNotificationId, notification)
    }

    companion object {
        const val ActionDaily = "com.example.flowerworld.action.DAILY_SUMMARY"
        const val ChannelId = "watering"
        const val SummaryNotificationId = 1

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

/** Alarms are lost on reboot: sets the daily one again. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        launchAsync(context) { container ->
            container.reminderScheduler.sync(container.plantRepository.plants.first())
        }
    }
}
