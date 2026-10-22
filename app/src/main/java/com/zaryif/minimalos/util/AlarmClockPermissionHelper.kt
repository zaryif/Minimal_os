package com.zaryif.minimalos.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings

/**
 * Robust helper for alarm clock intents, next alarm retrieval, and exact alarm permission management.
 * Handles OEM-specific restrictions and Android version differences (Android 8.0 through Android 15+).
 */
object AlarmClockPermissionHelper {

    /**
     * Checks whether the app is allowed to schedule exact alarms or access alarm clock capabilities.
     * On Android 12 (API 31)+, requires user-granted or system-whitelisted exact alarm permission.
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            return try {
                alarmManager?.canScheduleExactAlarms() ?: true
            } catch (_: Exception) {
                true
            }
        }
        return true
    }

    /**
     * Directs the user to system settings to grant exact alarm permission if required.
     */
    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                // Fallback to Application Details settings if specific exact alarm screen is unavailable
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Safely queries the next scheduled system alarm trigger time in milliseconds.
     * Returns 0L if no alarm is scheduled or if permission is restricted on specific OEM builds.
     */
    fun getNextAlarmTriggerTime(context: Context): Long {
        return try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.nextAlarmClock?.triggerTime ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Opens the native device Clock or Alarms application.
     * Attempts standard AlarmClock intents first, followed by vendor-specific clock package fallbacks.
     */
    fun openClockApp(context: Context) {
        val intents = listOf(
            Intent(AlarmClock.ACTION_SHOW_ALARMS),
            Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            }
        )
        for (intent in intents) {
            try {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }

        // OEM clock package fallbacks (Google, Samsung, OnePlus/ColorOS, Xiaomi, Vivo, AOSP)
        val knownClockPackages = listOf(
            "com.google.android.deskclock",
            "com.sec.android.app.clockpackage",
            "com.coloros.alarmclock",
            "com.oppo.alarmclock",
            "com.vivo.clock",
            "com.oneplus.deskclock",
            "com.android.deskclock"
        )
        for (pkg in knownClockPackages) {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                    return
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Sets a system alarm for the specified hour and minute.
     * Verifies exact alarm permissions on Android 12+ and falls back to visible UI if background action is restricted.
     */
    fun setAlarm(
        context: Context,
        hour: Int,
        minute: Int,
        label: String = "Minimal Alarm"
    ): Result<Unit> {
        if (!canScheduleExactAlarms(context)) {
            requestExactAlarmPermission(context)
        }

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(intent)
            Result.success(Unit)
        } catch (_: Exception) {
            // Fallback: Show clock UI with pre-filled alarm time
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            try {
                context.startActivity(intent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Sets a countdown timer for the specified duration in seconds.
     */
    fun setTimer(
        context: Context,
        seconds: Int,
        label: String = "Minimal Timer"
    ): Result<Unit> {
        if (!canScheduleExactAlarms(context)) {
            requestExactAlarmPermission(context)
        }

        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(intent)
            Result.success(Unit)
        } catch (_: Exception) {
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            try {
                context.startActivity(intent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
