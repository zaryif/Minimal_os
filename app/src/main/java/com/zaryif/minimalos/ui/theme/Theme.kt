package com.zaryif.minimalos.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MinimalColorScheme = darkColorScheme(
    primary = MinimalRed,
    onPrimary = MinimalWhite,
    secondary = MinimalDarkGray,
    onSecondary = MinimalWhite,
    tertiary = MinimalGray,
    background = MinimalBlack,
    onBackground = MinimalWhite,
    surface = MinimalCardBg,
    onSurface = MinimalWhite,
    surfaceVariant = MinimalDarkGray,
    onSurfaceVariant = MinimalGray,
    outline = MinimalDarkGray,
    error = MinimalRed,
    onError = MinimalWhite,
)

@Composable
fun MinimalOSTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MinimalBlack.toArgb()
            window.navigationBarColor = MinimalBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = MinimalColorScheme,
        typography = MinimalTypography,
        content = content
    )
}
