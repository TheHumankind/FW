package com.example.flowerworld.reminder

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Opens the system screens where the user lets reminders survive in the background.
 * Huawei/Honor firmwares kill background work unless the app is excluded from battery
 * optimization and allowed to autostart.
 */
object ReminderSettings {
    /** True on Huawei/Honor, whose "autostart" screen is not part of Android and needs its own intent. */
    val hasAutostartScreen: Boolean
        get() = Build.MANUFACTURER.lowercase() in HuaweiFamily

    /** Opens the list of apps with battery optimization (the app can be set to "don't optimize" there). */
    fun openBatterySettings(context: Context) {
        launchFirst(context, Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }

    /** Opens the autostart / launch management screen; falls back to the app's own settings page. */
    fun openAutostartSettings(context: Context) {
        val candidates = AutostartScreens.map { (pkg, cls) -> Intent().setComponent(ComponentName(pkg, cls)) }
        launchFirst(context, *candidates.toTypedArray(), appDetails(context))
    }

    private fun appDetails(context: Context) = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )

    /** Starts the first intent that some activity can handle; does nothing if none can. */
    private fun launchFirst(context: Context, vararg intents: Intent) {
        for (intent in intents) {
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return
            } catch (_: ActivityNotFoundException) {
                // try the next one
            } catch (_: SecurityException) {
                // the screen exists but is not exported to us
            }
        }
    }

    private val HuaweiFamily = setOf("huawei", "honor")

    // Known launch-management screens of EMUI / Magic UI, newest first
    private val AutostartScreens = listOf(
        "com.huawei.systemmanager" to "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
        "com.huawei.systemmanager" to "com.huawei.systemmanager.optimize.process.ProtectActivity",
        "com.hihonor.systemmanager" to "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
    )
}
