package com.zaryif.minimalos.domain.usecase

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.zaryif.minimalos.data.repository.AppRepository
import com.zaryif.minimalos.data.repository.ContactRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.CommandType
import com.zaryif.minimalos.domain.parser.CommandParser
import java.util.Calendar
import java.util.TimeZone

sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class LaunchedApp(val appName: String) : CommandResult()
    data class Error(val error: String) : CommandResult()
}

class ExecuteCommandUseCase(
    private val context: Context,
    private val appRepository: AppRepository,
    private val storageRepository: StorageRepository,
    private val contactRepository: ContactRepository
) {

    fun execute(rawInput: String): CommandResult {
        if (rawInput.isBlank()) {
            return CommandResult.Error("Empty command")
        }

        val command = CommandParser.parse(rawInput)

        return when (command.type) {
            CommandType.MESSAGE -> handleMessage(command.argument)
            CommandType.CALL -> handleCall(command.argument)
            CommandType.TODO -> handleTodo(command.argument)
            CommandType.NOTE -> handleNote(command.argument)
            CommandType.EVENT -> handleEvent(command.argument)
            CommandType.TIMER -> handleTimer(command.argument)
            CommandType.ALARM -> handleAlarm(command.argument)
            CommandType.ASK -> handleAsk(command.argument)
            CommandType.DRIVE -> handleDrive(command.argument)
            CommandType.EMAIL -> handleEmail(command.argument)
            CommandType.SEARCH -> handleAppSearch(command.argument)
        }
    }

    private fun handleMessage(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter recipient or message")

        val parts = arg.split(" ", limit = 2)
        var targetNumber = ""
        var body = arg

        // Check if first word is a contact name
        if (parts.size >= 2) {
            val matchedContacts = contactRepository.searchContacts(parts[0])
            if (matchedContacts.isNotEmpty()) {
                targetNumber = matchedContacts.first().phoneNumber
                body = parts[1]
            } else if (parts[0].all { it.isDigit() || it == '+' || it == '-' }) {
                targetNumber = parts[0]
                body = parts[1]
            }
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = if (targetNumber.isNotEmpty()) {
                Uri.parse("smsto:$targetNumber")
            } else {
                Uri.parse("smsto:")
            }
            putExtra("sms_body", body)
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening messages…")
        } catch (e: Exception) {
            CommandResult.Error("Could not open messages: ${e.message}")
        }
    }

    private fun handleCall(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter contact name or phone number")

        var phoneNumber = arg.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }

        // If not a raw number, search contacts
        if (phoneNumber.length < 3) {
            val contacts = contactRepository.searchContacts(arg)
            if (contacts.isNotEmpty()) {
                phoneNumber = contacts.first().phoneNumber
            }
        }

        if (phoneNumber.isBlank()) {
            return CommandResult.Error("No contact found for '$arg'")
        }

        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        val intent = if (hasCallPermission) {
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        } else {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(intent)
            CommandResult.Success("Calling $phoneNumber…")
        } catch (e: Exception) {
            CommandResult.Error("Could not place call: ${e.message}")
        }
    }

    private fun handleTodo(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter to-do task")
        storageRepository.addTodo(arg)

        // Also broadcast or notify Google Tasks if available
        return CommandResult.Success("Saved to-do: $arg")
    }

    private fun handleNote(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter note content")

        // 1. Always save in Minimal Memo storage
        storageRepository.addNote(arg)

        // 2. Seamless Google Keep integration: dispatch share intent directly to Keep if installed
        val keepPackage = "com.google.android.keep"
        val pm = context.packageManager
        val isKeepInstalled = try {
            pm.getPackageInfo(keepPackage, 0)
            true
        } catch (_: Exception) {
            false
        }

        if (isKeepInstalled) {
            try {
                val keepIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage(keepPackage)
                    putExtra(Intent.EXTRA_TEXT, arg)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(keepIntent)
                return CommandResult.Success("Saved note & synced with Google Keep")
            } catch (_: Exception) {}
        }

        return CommandResult.Success("Saved note: $arg")
    }

    private fun handleEvent(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter event title")

        val hasCalendarPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        // If permission granted, attempt silent insert into Google Calendar provider
        if (hasCalendarPermission) {
            try {
                val startMillis = Calendar.getInstance().apply {
                    add(Calendar.HOUR_OF_DAY, 1)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis
                val endMillis = startMillis + (60 * 60 * 1000)

                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(CalendarContract.Events.TITLE, arg)
                    put(CalendarContract.Events.CALENDAR_ID, 1)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
                val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                if (uri != null) {
                    return CommandResult.Success("Event '$arg' added to Calendar")
                }
            } catch (_: Exception) {}
        }

        // Fallback: Open Google Calendar / default calendar pre-filled
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, arg)
            setPackage("com.google.android.calendar")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening Google Calendar for '$arg'")
        } catch (_: Exception) {
            // General calendar fallback
            val fallbackIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, arg)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallbackIntent)
                CommandResult.Success("Creating event: $arg")
            } catch (e: Exception) {
                CommandResult.Error("Could not open calendar: ${e.message}")
            }
        }
    }

    private fun handleTimer(arg: String): CommandResult {
        val lower = arg.lowercase().trim()
        val seconds = when {
            lower.endsWith("s") -> lower.dropLast(1).toIntOrNull() ?: 60
            lower.endsWith("m") -> (lower.dropLast(1).toIntOrNull() ?: 5) * 60
            lower.endsWith("h") -> (lower.dropLast(1).toIntOrNull() ?: 1) * 3600
            else -> (lower.toIntOrNull() ?: 5) * 60
        }

        // Silent direct timer execution with EXTRA_SKIP_UI = true
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Minimal Timer")
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Timer set for ${seconds / 60}m ${seconds % 60}s (silent)")
        } catch (_: Exception) {
            // Fallback without skip_ui
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            try {
                context.startActivity(intent)
                CommandResult.Success("Set timer for ${seconds / 60}m")
            } catch (e: Exception) {
                CommandResult.Error("Could not set timer: ${e.message}")
            }
        }
    }

    private fun handleAlarm(arg: String): CommandResult {
        var hour = 7
        var minute = 0
        try {
            val clean = arg.lowercase().replace(" ", "")
            val isPm = clean.contains("pm")
            val rawTime = clean.replace("am", "").replace("pm", "")
            if (rawTime.contains(":")) {
                val parts = rawTime.split(":")
                hour = parts[0].toInt()
                minute = parts[1].toInt()
            } else {
                hour = rawTime.toInt()
            }
            if (isPm && hour < 12) hour += 12
        } catch (_: Exception) {}

        // Silent direct alarm execution with EXTRA_SKIP_UI = true
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Minimal Alarm")
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Alarm set for %02d:%02d (silent)".format(hour, minute))
        } catch (_: Exception) {
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            try {
                context.startActivity(intent)
                CommandResult.Success("Alarm set for %02d:%02d".format(hour, minute))
            } catch (e: Exception) {
                CommandResult.Error("Could not set alarm: ${e.message}")
            }
        }
    }

    private fun handleDrive(arg: String): CommandResult {
        val drivePackage = "com.google.android.apps.docs"
        val query = if (arg.isBlank()) "" else arg
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://drive.google.com/drive/search?q=${Uri.encode(query)}")
            setPackage(drivePackage)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Searching Drive for '$query'")
        } catch (_: Exception) {
            val fallback = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://drive.google.com/drive/search?q=${Uri.encode(query)}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            try {
                context.startActivity(fallback)
                CommandResult.Success("Opening Drive search")
            } catch (e: Exception) {
                CommandResult.Error("Could not open Drive: ${e.message}")
            }
        }
    }

    private fun handleEmail(arg: String): CommandResult {
        val gmailPackage = "com.google.android.gm"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, arg)
            setPackage(gmailPackage)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening Gmail compose…")
        } catch (_: Exception) {
            val fallback = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallback)
                CommandResult.Success("Opening email client…")
            } catch (e: Exception) {
                CommandResult.Error("Could not open email: ${e.message}")
            }
        }
    }

    private fun handleAsk(arg: String): CommandResult {
        val query = if (arg.isBlank()) "minimalism" else arg
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra("query", query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Searching '$query'…")
        } catch (_: Exception) {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            try {
                context.startActivity(browserIntent)
                CommandResult.Success("Searching '$query'…")
            } catch (e2: Exception) {
                CommandResult.Error("Could not search: ${e2.message}")
            }
        }
    }

    private fun handleAppSearch(query: String): CommandResult {
        val apps = appRepository.getInstalledApps()
        val matches = appRepository.searchApps(query, apps)
        if (matches.isEmpty()) {
            return CommandResult.Error("No app matching '$query'")
        }
        val target = matches.first()
        val launchIntent = appRepository.getLaunchIntent(target.packageName)
        return if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
            CommandResult.LaunchedApp(target.label)
        } else {
            CommandResult.Error("Cannot launch ${target.label}")
        }
    }
}
