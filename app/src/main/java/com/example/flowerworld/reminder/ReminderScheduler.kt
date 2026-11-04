package com.example.flowerworld.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.example.flowerworld.domain.model.Plant
import java.time.Clock
import java.util.concurrent.TimeUnit

/**
 * Sets one alarm per plant that fires at the plant's next watering time.
 *
 * Alarms are exact (`setExactAndAllowWhileIdle`) when the system allows it, because Honor/Huawei
 * firmwares delay inexact background work; otherwise they fall back to the inexact variant.
 * Re-setting an alarm for the same plant replaces the previous one. A plant that is missing from
 * the list passed to [sync] (deleted) has its alarm and notification cancelled.
 */
class ReminderScheduler(
    private val context: Context,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    private val alarmManager = context.getSystemService<AlarmManager>()!!

    /**
     * Makes the alarms match [plants]. An overdue plant keeps its pending repeat alarm; if there
     * is none (e.g. after a reboot cleared all alarms) it is reminded soon.
     */
    fun sync(plants: List<Plant>) {
        val now = clock.millis()
        val currentIds = plants.mapTo(mutableSetOf()) { it.id }
        // plants that were deleted since the last sync lose their alarm and notification
        for (removedId in scheduledIds() - currentIds) cancel(removedId)
        saveScheduledIds(currentIds)
        for (plant in plants) {
            when {
                plant.nextWateringAt > now -> {
                    scheduleAt(plant.id, plant.nextWateringAt)
                    // watered (in the app or from the notification): the old reminder is obsolete
                    NotificationManagerCompat.from(context).cancel(ReminderReceiver.notificationId(plant.id))
                }
                !hasAlarm(plant.id) -> scheduleAt(plant.id, now + OverdueDelayMillis)
            }
        }
    }

    private fun cancel(plantId: String) {
        pendingIntent(plantId, PendingIntent.FLAG_NO_CREATE)?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
        NotificationManagerCompat.from(context).cancel(ReminderReceiver.notificationId(plantId))
    }

    // ids of the plants that had alarms at the last sync; kept on disk so a restart does not forget them
    private val prefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)

    private fun scheduledIds(): Set<String> = prefs.getStringSet(KeyScheduledIds, emptySet()).orEmpty()

    private fun saveScheduledIds(ids: Set<String>) {
        prefs.edit().putStringSet(KeyScheduledIds, ids).apply()
    }

    /** Sets (or replaces) the alarm of the plant with [plantId] to fire at [atMillis]. */
    fun scheduleAt(plantId: String, atMillis: Long) {
        val intent = pendingIntent(plantId, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        val canBeExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canBeExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, intent)
        }
    }

    /** Schedules the next reminder for a plant that was not watered after a reminder. */
    fun scheduleRepeat(plantId: String) = scheduleAt(plantId, clock.millis() + RepeatDelayMillis)

    private fun hasAlarm(plantId: String): Boolean =
        pendingIntent(plantId, PendingIntent.FLAG_NO_CREATE) != null

    private fun pendingIntent(plantId: String, flag: Int): PendingIntent? {
        val intent = Intent(context, ReminderReceiver::class.java)
            .setAction(ReminderReceiver.ActionRemind)
            .putExtra(ReminderReceiver.ExtraPlantId, plantId)
        return PendingIntent.getBroadcast(
            context,
            plantId.hashCode(),
            intent,
            flag or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val PrefsName = "reminders"
        const val KeyScheduledIds = "scheduledPlantIds"

        /** Delay before the first reminder for an already overdue plant that has no alarm. */
        const val OverdueDelayMillis = 60_000L

        /** How long to wait before reminding again if the user did not water the plant. */
        val RepeatDelayMillis = TimeUnit.HOURS.toMillis(6)
    }
}
