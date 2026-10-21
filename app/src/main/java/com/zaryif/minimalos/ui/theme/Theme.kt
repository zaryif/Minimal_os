package com.zaryif.minimalos.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun MinimalOSTheme(
    themeId: String = "RABBIT_ORANGE",
    content: @Composable () -> Unit
) {
    // Update active theme state
    currentActiveTheme = MinimalThemeRegistry.getTheme(themeId)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            activity?.window?.let { window ->
                window.statusBarColor = currentActiveTheme.background.toArgb()
                window.navigationBarColor = currentActiveTheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    val colorScheme = darkColorScheme(
        primary = currentActiveTheme.accent,
        onPrimary = currentActiveTheme.textPrimary,
        secondary = currentActiveTheme.border,
        onSecondary = currentActiveTheme.textPrimary,
        tertiary = currentActiveTheme.textSecondary,
        background = currentActiveTheme.background,
        onBackground = currentActiveTheme.textPrimary,
        surface = currentActiveTheme.card,
        onSurface = currentActiveTheme.textPrimary,
        surfaceVariant = currentActiveTheme.border,
        onSurfaceVariant = currentActiveTheme.textSecondary,
        outline = currentActiveTheme.border,
        error = currentActiveTheme.accent,
        onError = currentActiveTheme.textPrimary,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MinimalTypography,
        content = content
    )
}
