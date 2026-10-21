package com.zaryif.minimalos.domain.parser

import com.zaryif.minimalos.domain.model.Command
import com.zaryif.minimalos.domain.model.CommandType

object CommandParser {

    fun parse(input: String): Command {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Command(
                type = CommandType.SEARCH,
                rawInput = input,
                argument = ""
            )
        }

        // 1. Check for single symbol prefix
        val firstChar = trimmed.first()
        val symbolCommand = CommandType.fromSymbol(firstChar)
        if (symbolCommand != null) {
            var arg = trimmed.drop(1).trim()
            val redundantPrefixes = when (symbolCommand) {
                CommandType.NOTE -> listOf("note ", "notes ", "memo ", "keep ")
                CommandType.TODO -> listOf("todo ", "task ", "reminder ", "remind ")
                CommandType.CALL -> listOf("call ", "dial ")
                CommandType.MESSAGE -> listOf("msg ", "sms ", "text ", "message ")
                CommandType.EVENT -> listOf("event ", "cal ", "calendar ", "schedule ")
                CommandType.ALARM -> listOf("set alarm ", "alarm ")
                CommandType.TIMER -> listOf("set timer ", "timer ")
                CommandType.DRIVE -> listOf("drive ")
                CommandType.EMAIL -> listOf("mail ", "email ")
                CommandType.WHATSAPP -> listOf("whatsapp ", "wa ")
                CommandType.ASK -> listOf("ask ", "search ", "google ", "find ")
                CommandType.HELP -> listOf("help ", "commands ", "guide ")
                else -> emptyList()
            }
            for (p in redundantPrefixes) {
                if (arg.lowercase().startsWith(p)) {
                    arg = arg.substring(p.length).trim()
                    break
                }
            }
            return Command(
                type = symbolCommand,
                rawInput = input,
                argument = arg
            )
        }

        val lower = trimmed.lowercase()

        // 2. Check for help commands
        if (lower == "help" || lower.startsWith("help ") || lower == "commands" || lower == "man" || lower == "guide") {
            return Command(
                type = CommandType.HELP,
                rawInput = input,
                argument = ""
            )
        }

        // 3. Check for natural language keyword prefixes
        val keywordMatches = listOf(
            PrefixMapping(listOf("set alarm ", "alarm "), CommandType.ALARM),
            PrefixMapping(listOf("set timer ", "timer "), CommandType.TIMER),
            PrefixMapping(listOf("call ", "dial "), CommandType.CALL),
            PrefixMapping(listOf("whatsapp ", "wa "), CommandType.WHATSAPP),
            PrefixMapping(listOf("msg ", "sms ", "text ", "message "), CommandType.MESSAGE),
            PrefixMapping(listOf("note ", "notes ", "keep ", "memo "), CommandType.NOTE),
            PrefixMapping(listOf("todo ", "task ", "remind ", "reminder "), CommandType.TODO),
            PrefixMapping(listOf("event ", "cal ", "calendar ", "schedule "), CommandType.EVENT),
            PrefixMapping(listOf("drive "), CommandType.DRIVE),
            PrefixMapping(listOf("mail ", "email "), CommandType.EMAIL),
            PrefixMapping(listOf("ask ", "search ", "google ", "find "), CommandType.ASK)
        )

        for (mapping in keywordMatches) {
            for (prefix in mapping.prefixes) {
                if (lower.startsWith(prefix)) {
                    val arg = trimmed.substring(prefix.length).trim()
                    return Command(
                        type = mapping.commandType,
                        rawInput = input,
                        argument = arg
                    )
                }
            }
        }

        // 4. Fallback: app search
        return Command(
            type = CommandType.SEARCH,
            rawInput = input,
            argument = trimmed
        )
    }

    private data class PrefixMapping(
        val prefixes: List<String>,
        val commandType: CommandType
    )
}
