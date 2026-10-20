package com.zaryif.minimalos.ui.settings

import android.app.WallpaperManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.RestrictionMode
import com.zaryif.minimalos.ui.components.MinimalCard
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@Composable
fun SettingsScreen(
    currentMode: RestrictionMode,
    onSelectMode: (RestrictionMode) -> Unit,
    onResetOnboarding: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MinimalWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "settings",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Restriction Mode
            Text(
                text = "RESTRICTION MODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            MinimalCard(
                title = "monk mode",
                subtitle = "essentials only, grayscale always",
                isSelected = currentMode == RestrictionMode.MONK,
                onClick = { onSelectMode(RestrictionMode.MONK) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            MinimalCard(
                title = "focus",
                subtitle = "everything, distracting apps on a clock",
                isSelected = currentMode == RestrictionMode.FOCUS,
                onClick = { onSelectMode(RestrictionMode.FOCUS) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            MinimalCard(
                title = "unrestricted",
                subtitle = "everything, no guardrails",
                isSelected = currentMode == RestrictionMode.UNRESTRICTED,
                onClick = { onSelectMode(RestrictionMode.UNRESTRICTED) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Section: Appearance & Wallpaper
            Text(
                text = "CUSTOMIZATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "change wallpaper",
                subtitle = "set AMOLED black or custom photo wallpaper",
                onClick = {
                    try {
                        val intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                        context.startActivity(Intent.createChooser(intent, "Choose Wallpaper"))
                    }
                }
            )

            HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)

            SettingsActionRow(
                title = "set as default launcher",
                subtitle = "make Minimal OS your primary home",
                onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            )

            HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)

            SettingsActionRow(
                title = "notification access",
                subtitle = "grant permission for unified hub alerts",
                onClick = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)

            SettingsActionRow(
                title = "replay onboarding",
                subtitle = "walk through the introduction guide again",
                onClick = onResetOnboarding
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Section: Command Symbols & Keywords Reference
            Text(
                text = "COMMAND REFERENCE (SYMBOLS & WORDS)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            val symbols = listOf(
                Pair("@ or msg", "message — compose SMS directly to contact"),
                Pair("# or call", "call — dial phone number or contact directly"),
                Pair("- or todo", "to-do — add task item to Minimal Memo"),
                Pair("! or note", "note — saves to Minimal Memo & Google Keep"),
                Pair("* or event", "event — schedule event directly in Google Calendar"),
                Pair("+ or timer", "timer — set countdown timer instantly"),
                Pair(": or alarm", "alarm — set alarm instantly (e.g. :7:30am)"),
                Pair("^ or drive", "drive — search inside Google Drive"),
                Pair("& or mail", "email — compose message via Gmail"),
                Pair("? or ask", "ask — web query without opening apps"),
                Pair("(text)", "search installed apps & launch on Enter")
            )

            symbols.forEach { (sym, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sym,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        modifier = Modifier.width(90.dp)
                    )
                    Text(
                        text = desc,
                        fontSize = 13.sp,
                        color = MinimalGray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // About
            Text(
                text = "ABOUT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Minimal OS Launcher v1.1.0\nCreated by MD. Zarif Azfar\nPure text-first minimalist experience with full terminal capabilities.",
                fontSize = 13.sp,
                color = MinimalGray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MinimalGray
        )
    }
}
