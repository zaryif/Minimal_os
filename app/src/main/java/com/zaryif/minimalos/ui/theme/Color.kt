package com.zaryif.minimalos.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

data class MinimalTheme(
    val id: String,
    val name: String,
    val background: Color,
    val card: Color,
    val darkCard: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color
)

object MinimalThemeRegistry {
    val THEMES = listOf(
        MinimalTheme("AMOLED_RED", "AMOLED Crimson", Color(0xFF000000), Color(0xFF111111), Color(0xFF0A0A0A), Color(0xFF262626), Color(0xFFFFFFFF), Color(0xFF8E8E93), Color(0xFFFF3B30)),
        MinimalTheme("SPACE_GRAY", "Apple Space Gray", Color(0xFF141416), Color(0xFF1C1C1E), Color(0xFF161618), Color(0xFF2C2C2E), Color(0xFFFFFFFF), Color(0xFF98989D), Color(0xFF0A84FF)),
        MinimalTheme("NORDIC_FROST", "Nordic Snow", Color(0xFF000000), Color(0xFF141414), Color(0xFF0D0D0D), Color(0xFF2E2E2E), Color(0xFFFFFFFF), Color(0xFFA0A0A0), Color(0xFFFFFFFF)),
        MinimalTheme("RABBIT_ORANGE", "Rabbit Amber", Color(0xFF0D0D0F), Color(0xFF18181B), Color(0xFF121214), Color(0xFF2D2D32), Color(0xFFFFFFFF), Color(0xFF9E9EA4), Color(0xFFFF5722)),
        MinimalTheme("EMERALD_MATRIX", "Matrix Forest", Color(0xFF070E0A), Color(0xFF0E1A13), Color(0xFF08120B), Color(0xFF1C3224), Color(0xFFFFFFFF), Color(0xFF7EA88F), Color(0xFF10B981)),
        MinimalTheme("MIDNIGHT_NAVY", "Midnight Obsidian", Color(0xFF060913), Color(0xFF0D1424), Color(0xFF080C17), Color(0xFF1A2640), Color(0xFFFFFFFF), Color(0xFF8B9BB4), Color(0xFF38BDF8)),
        MinimalTheme("TITANIUM_VIOLET", "Titanium Purple", Color(0xFF0A0714), Color(0xFF151024), Color(0xFF0E0919), Color(0xFF261C3D), Color(0xFFFFFFFF), Color(0xFF9A8CB2), Color(0xFFA855F7))
    )

    fun getTheme(id: String): MinimalTheme {
        return THEMES.find { it.id.equals(id, ignoreCase = true) } ?: THEMES.first()
    }
}

var currentActiveTheme by mutableStateOf(MinimalThemeRegistry.THEMES.first())

val MinimalBlack: Color get() = currentActiveTheme.background
val MinimalWhite: Color get() = currentActiveTheme.textPrimary
val MinimalRed: Color get() = currentActiveTheme.accent
val MinimalGray: Color get() = currentActiveTheme.textSecondary
val MinimalDarkGray: Color get() = currentActiveTheme.border
val MinimalCardBg: Color get() = currentActiveTheme.card
val MinimalDarkCardBg: Color get() = currentActiveTheme.darkCard
