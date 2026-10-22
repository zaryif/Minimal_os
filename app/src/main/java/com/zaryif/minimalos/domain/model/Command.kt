package com.zaryif.minimalos.domain.model

enum class CommandType(val symbol: Char, val displayName: String) {
    MESSAGE('@', "message"),
    WHATSAPP('=', "whatsapp"),
    CALL('#', "call"),
    TODO('-', "to-do"),
    NOTE('!', "note"),
    EVENT('*', "event"),
    TIMER('+', "timer"),
    ALARM(':', "alarm"),
    ASK('?', "ask"),
    DRIVE('^', "drive"),
    EMAIL('&', "email"),
    YOUTUBE('$', "youtube"),
    POMODORO('%', "pomodoro"),
    UNINSTALL('/', "uninstall"),
    HELP('~', "help"),
    SEARCH('\u0000', "search");

    companion object {
        fun fromSymbol(char: Char): CommandType? {
            return entries.find { it.symbol == char }
        }
    }
}

data class Command(
    val type: CommandType,
    val rawInput: String,
    val argument: String,
    val targetAppPackage: String? = null
)
