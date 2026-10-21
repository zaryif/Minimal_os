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
import com.zaryif.minimalos.data.preferences.AiProvider
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.data.repository.AppRepository
import com.zaryif.minimalos.data.repository.ContactRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.CommandType
import com.zaryif.minimalos.domain.parser.CommandParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class LaunchedApp(val appName: String) : CommandResult()
    data class Error(val error: String) : CommandResult()
}

class ExecuteCommandUseCase(
    private val context: Context,
    private val appRepository: AppRepository,
    private val storageRepository: StorageRepository,
    private val contactRepository: ContactRepository,
    private val userPreferences: UserPreferences
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
            CommandType.SEARCH -> handleAppSearchOrWebFallback(command.argument)
        }
    }

    private fun handleMessage(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter recipient or message")

        val parts = arg.split(" ", limit = 2)
        var targetNumber = ""
        var body = arg

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
        return CommandResult.Success("Saved to-do: $arg")
    }

    private fun handleNote(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter note content")

        storageRepository.addNote(arg)

        // Google Keep instant sync
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

    /**
     * Enhanced Event Handler: Natural time parser parses phrases like:
     * "team standup at 4pm", "lunch at 12:30", "gym tomorrow at 8am"
     * and sets DTSTART & DTEND accurately!
     */
    private fun handleEvent(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter event title")

        val parsedTimeResult = parseNaturalEventTime(arg)
        val cleanTitle = parsedTimeResult.cleanTitle
        val startMillis = parsedTimeResult.startMillis
        val endMillis = startMillis + (60 * 60 * 1000) // Default 1 hour duration

        val hasCalendarPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        // Direct silent event insertion without needing user to click "done"
        if (hasCalendarPermission) {
            try {
                var calendarId = 1L
                val calCursor = context.contentResolver.query(
                    CalendarContract.Calendars.CONTENT_URI,
                    arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY),
                    null,
                    null,
                    null
                )
                calCursor?.use {
                    if (it.moveToFirst()) {
                        calendarId = it.getLong(0)
                    }
                }

                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(CalendarContract.Events.TITLE, cleanTitle)
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
                val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                if (uri != null) {
                    val timeStr = "%02d:%02d".format(
                        parsedTimeResult.calendar.get(Calendar.HOUR_OF_DAY),
                        parsedTimeResult.calendar.get(Calendar.MINUTE)
                    )
                    return CommandResult.Success("Event '$cleanTitle' at $timeStr set in Calendar")
                }
            } catch (_: Exception) {}
        }

        // Fallback: Open pre-filled Google Calendar with accurate start and end millis
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, cleanTitle)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            setPackage("com.google.android.calendar")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening Google Calendar for '$cleanTitle'")
        } catch (_: Exception) {
            val fallbackIntent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, cleanTitle)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallbackIntent)
                CommandResult.Success("Creating event: $cleanTitle")
            } catch (e: Exception) {
                CommandResult.Error("Could not open calendar: ${e.message}")
            }
        }
    }

    private data class ParsedEventTime(
        val cleanTitle: String,
        val startMillis: Long,
        val calendar: Calendar
    )

    private fun parseNaturalEventTime(rawInput: String): ParsedEventTime {
        val cal = Calendar.getInstance()
        var title = rawInput.trim()

        val lower = rawInput.lowercase(Locale.getDefault())

        // Check if "tomorrow" is mentioned
        if (lower.contains("tomorrow")) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            title = title.replace(Regex("(?i)\\btomorrow\\b"), "").trim()
        }

        // Match patterns like: "at 4:30pm", "at 4pm", "at 16:00", "at 4", "4pm", "11:30am"
        val timeRegex = Pattern.compile("(?i)\\b(?:at\\s+)?(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b")
        val matcher = timeRegex.matcher(lower)

        var foundTime = false
        var hour = cal.get(Calendar.HOUR_OF_DAY) + 1 // Default to next hour
        var minute = 0

        while (matcher.find()) {
            val rawHour = matcher.group(1)?.toIntOrNull()
            val rawMin = matcher.group(2)?.toIntOrNull() ?: 0
            val ampm = matcher.group(3)?.lowercase()

            if (rawHour != null && rawHour in 1..24) {
                hour = rawHour
                minute = rawMin
                if (ampm == "pm" && hour < 12) hour += 12
                if (ampm == "am" && hour == 12) hour = 0
                if (ampm == null && hour in 1..7) hour += 12 // Assume 2-7 means PM for events
                foundTime = true

                // Remove the time portion from title
                title = title.removeRange(matcher.start(), matcher.end()).trim()
                break
            }
        }

        // Clean up remaining dangling prepositions like "at" or "on"
        title = title.replace(Regex("(?i)\\b(at|on)\\s*$"), "").trim()
        if (title.isBlank()) title = "New Event"

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return ParsedEventTime(
            cleanTitle = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            startMillis = cal.timeInMillis,
            calendar = cal
        )
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
            CommandResult.Success("Timer set for ${seconds / 60}m ${seconds % 60}s (auto)")
        } catch (_: Exception) {
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
            CommandResult.Success("Alarm set for %02d:%02d (auto)".format(hour, minute))
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
        val query = arg.trim()

        // 1. If Gemini app is installed, launch it directly
        val geminiPkg = "com.google.android.apps.bard"
        val pm = context.packageManager
        val isGeminiInstalled = try {
            pm.getPackageInfo(geminiPkg, 0)
            true
        } catch (_: Exception) {
            false
        }

        if (isGeminiInstalled) {
            val intent = pm.getLaunchIntentForPackage(geminiPkg)
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (query.isNotEmpty()) {
                    intent.putExtra(Intent.EXTRA_TEXT, query)
                }
                try {
                    context.startActivity(intent)
                    return CommandResult.Success("Opening Google Gemini…")
                } catch (_: Exception) {}
            }
        }

        // 2. Direct Android Digital Assistant / Voice Assistant overlay
        if (query.isEmpty()) {
            val assistIntent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(assistIntent)
                return CommandResult.Success("Opening Assistant…")
            } catch (_: Exception) {
                val assistFallback = Intent(Intent.ACTION_ASSIST).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(assistFallback)
                    return CommandResult.Success("Opening Assistant…")
                } catch (_: Exception) {}
            }
        }

        // 3. Web Search with query directly launched
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(android.app.SearchManager.QUERY, query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(searchIntent)
            CommandResult.Success("Searching '$query'…")
        } catch (_: Exception) {
            val browserFallback = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            try {
                context.startActivity(browserFallback)
                CommandResult.Success("Opening search for '$query'")
            } catch (e: Exception) {
                CommandResult.Error("Could not launch assistant: ${e.message}")
            }
        }
    }


    /**
     * If user typed arbitrary natural text and it doesn't match an app name,
     * automatically search it in the default web browser instead of failing!
     */
    private fun handleAppSearchOrWebFallback(query: String): CommandResult {
        val apps = appRepository.getInstalledApps()
        val matches = appRepository.searchApps(query, apps)

        if (matches.isNotEmpty()) {
            val target = matches.first()
            val launchIntent = appRepository.getLaunchIntent(target.packageName)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
                return CommandResult.LaunchedApp(target.label)
            }
        }

        // Natural text fallback -> search in default browser seamlessly
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra("query", query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(searchIntent)
            CommandResult.Success("Searching '$query' in browser…")
        } catch (_: Exception) {
            val browserFallback = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            try {
                context.startActivity(browserFallback)
                CommandResult.Success("Searching '$query' in browser…")
            } catch (e: Exception) {
                CommandResult.Error("Could not search web: ${e.message}")
            }
        }
    }
}
