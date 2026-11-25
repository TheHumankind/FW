package com.example.flowerworld.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.example.flowerworld.domain.model.Plant
import com.example.flowerworld.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.ZonedDateTime

/**
 * Keeps one repeating daily alarm that shows the watering summary (see [WateringSummary]).
 *
 * The alarm is exact (`setExactAndAllowWhileIdle`) when the system allows it, because Honor/Huawei
 * firmwares delay inexact background work; otherwise it falls back to the inexact variant.
 * Every firing plans the next day, so a missed or ignored notification is simply repeated tomorrow.
 */
class ReminderScheduler(
    private val context: Context,
    private val settings: SettingsRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    private val alarmManager = context.getSystemService<AlarmManager>()!!

    fun nowMillis(): Long = clock.millis()

    /** Makes sure the daily alarm is set and hides the summary once nothing needs watering any more. */
    suspend fun sync(plants: List<Plant>) {
        scheduleNextDaily()
        if (WateringSummary.of(plants, nowMillis()).isEmpty) {
            NotificationManagerCompat.from(context).cancel(ReminderReceiver.SummaryNotificationId)
        }
    }

    /** Sets (or replaces) the alarm for the next occurrence of the time chosen in the settings (device time zone). */
    suspend fun scheduleNextDaily() {
        val now = ZonedDateTime.now(clock)
        var next = now.with(settings.reminderTime.first())
        if (!next.isAfter(now)) next = next.plusDays(1)
        val atMillis = next.toInstant().toEpochMilli()
        val intent = dailyIntent()
        val canBeExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canBeExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        }
    }

    private fun dailyIntent(): PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, ReminderReceiver::class.java).setAction(ReminderReceiver.ActionDaily),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
