package com.zaryif.minimalos.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import com.zaryif.minimalos.data.repository.AppRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.Command
import com.zaryif.minimalos.domain.model.CommandType
import com.zaryif.minimalos.domain.parser.CommandParser

sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class LaunchedApp(val appName: String) : CommandResult()
    data class Error(val error: String) : CommandResult()
}

class ExecuteCommandUseCase(
    private val context: Context,
    private val appRepository: AppRepository,
    private val storageRepository: StorageRepository
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
            CommandType.SEARCH -> handleAppSearch(command.argument)
        }
    }

    private fun handleMessage(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter recipient or message")

        // Format can be: "@john meet at 5" or "@1234567890 hello" or just "@hello"
        val parts = arg.split(" ", limit = 2)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (parts.size == 2 && (parts[0].all { it.isDigit() || it == '+' || it == '-' })) {
                data = Uri.parse("smsto:${parts[0]}")
                putExtra("sms_body", parts[1])
            } else {
                data = Uri.parse("smsto:")
                putExtra("sms_body", arg)
            }
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening messages…")
        } catch (e: Exception) {
            CommandResult.Error("Could not open messages: ${e.message}")
        }
    }

    private fun handleCall(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter phone number or contact")
        val cleanNumber = arg.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        val intent = if (cleanNumber.isNotEmpty()) {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber"))
        } else {
            Intent(Intent.ACTION_DIAL)
        }.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Opening dialer for $arg…")
        } catch (e: Exception) {
            CommandResult.Error("Could not initiate call: ${e.message}")
        }
    }

    private fun handleTodo(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter to-do task")
        storageRepository.addTodo(arg)
        return CommandResult.Success("Added to-do: $arg")
    }

    private fun handleNote(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter note content")
        storageRepository.addNote(arg)
        return CommandResult.Success("Saved note: $arg")
    }

    private fun handleEvent(arg: String): CommandResult {
        if (arg.isBlank()) return CommandResult.Error("Enter event title")
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, arg)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Creating event: $arg")
        } catch (e: Exception) {
            CommandResult.Error("Could not open calendar: ${e.message}")
        }
    }

    private fun handleTimer(arg: String): CommandResult {
        // Parse "5m", "30s", "1h", or pure number (treated as minutes)
        val lower = arg.lowercase().trim()
        val seconds = when {
            lower.endsWith("s") -> lower.dropLast(1).toIntOrNull() ?: 60
            lower.endsWith("m") -> (lower.dropLast(1).toIntOrNull() ?: 5) * 60
            lower.endsWith("h") -> (lower.dropLast(1).toIntOrNull() ?: 1) * 3600
            else -> (lower.toIntOrNull() ?: 5) * 60
        }

        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Minimal Timer")
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Set timer for ${seconds / 60}m ${seconds % 60}s")
        } catch (e: Exception) {
            CommandResult.Error("Could not set timer: ${e.message}")
        }
    }

    private fun handleAlarm(arg: String): CommandResult {
        // Parse time like "7:30", "07:30am", "8pm", "8"
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

        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, "Minimal Alarm")
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            CommandResult.Success("Alarm set for %02d:%02d".format(hour, minute))
        } catch (e: Exception) {
            CommandResult.Error("Could not set alarm: ${e.message}")
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
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(browserIntent)
                CommandResult.Success("Searching '$query'…")
            } catch (e2: Exception) {
                CommandResult.Error("Could not perform search: ${e2.message}")
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
