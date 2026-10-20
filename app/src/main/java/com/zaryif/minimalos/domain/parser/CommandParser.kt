package com.zaryif.minimalos.domain.parser

import com.zaryif.minimalos.domain.model.Command
import com.zaryif.minimalos.domain.model.CommandType

object CommandParser {

    fun parse(input: String): Command {
        if (input.isBlank()) {
            return Command(
                type = CommandType.SEARCH,
                rawInput = input,
                argument = ""
            )
        }

        val firstChar = input.first()
        val commandType = CommandType.fromSymbol(firstChar)

        return if (commandType != null) {
            Command(
                type = commandType,
                rawInput = input,
                argument = input.drop(1).trim()
            )
        } else {
            Command(
                type = CommandType.SEARCH,
                rawInput = input,
                argument = input.trim()
            )
        }
    }
}
