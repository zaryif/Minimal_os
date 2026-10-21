package com.zaryif.minimalos.ui.settings

import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zaryif.minimalos.data.preferences.AiProvider
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    installedApps: List<AppInfo>,
    userPreferences: UserPreferences,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedAiProvider by userPreferences.aiProvider.collectAsState(initial = AiProvider.GEMINI)
    val customWallpaperUri by userPreferences.customWallpaperUri.collectAsState(initial = null)
    val favoriteAppPackages by userPreferences.favoriteApps.collectAsState(initial = emptySet())

    var showAppPickerModal by remember { mutableStateOf(false) }

    // Photo Gallery Wallpaper Picker
    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        scope.launch {
            userPreferences.setCustomWallpaperUri(uri?.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
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

            // SECTION 1: HOME SCREEN APP SHORTCUTS
            Text(
                text = "HOME SCREEN APP SHORTCUTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val favoriteAppsList = remember(favoriteAppPackages, installedApps) {
                installedApps.filter { it.packageName in favoriteAppPackages }
            }

            if (favoriteAppsList.isEmpty()) {
                Text(
                    text = "No shortcuts pinned to Home. Tap below to choose apps.",
                    fontSize = 13.sp,
                    color = MinimalGray
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favoriteAppsList.take(5).forEach { app ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MinimalDarkCardBg)
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = app.label,
                                fontSize = 12.sp,
                                color = MinimalWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MinimalRed)
                    .clickable { showAppPickerModal = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (favoriteAppsList.isEmpty()) "+ CHOOSE HOME SHORTCUTS" else "EDIT HOME SHORTCUTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 2: AI ASSISTANT PROVIDER
            Text(
                text = "DEFAULT ASSISTANT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiProvider.entries.forEach { provider ->
                    val isSelected = provider == selectedAiProvider
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                            .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                            .clickable {
                                scope.launch { userPreferences.setAiProvider(provider) }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = provider.displayName.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 3: WALLPAPER & APPEARANCE
            Text(
                text = "WALLPAPER & APPEARANCE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionRow(
                title = "choose wallpaper from photos",
                subtitle = if (customWallpaperUri != null) "custom photo active (tap to change)" else "select an image from your gallery",
                onClick = { wallpaperPickerLauncher.launch("image/*") }
            )

            if (customWallpaperUri != null) {
                HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)
                SettingsActionRow(
                    title = "reset to pure AMOLED black",
                    subtitle = "restore default zero-distraction black background",
                    onClick = { scope.launch { userPreferences.setCustomWallpaperUri(null) } }
                )
            }

            HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)

            SettingsActionRow(
                title = "system live wallpaper picker",
                subtitle = "launch Android system wallpaper settings",
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

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 4: SYSTEM CONFIGURATION
            Text(
                text = "SYSTEM CONFIGURATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 5: COMMAND REFERENCE
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
                Pair("* or event", "event — schedule directly in Calendar with natural time"),
                Pair("+ or timer", "timer — set countdown timer instantly"),
                Pair(": or alarm", "alarm — set alarm instantly (e.g. :7:30am)"),
                Pair("^ or drive", "drive — search inside Google Drive"),
                Pair("& or mail", "email — compose message via Gmail"),
                Pair("? or ask", "assistant — voice or chat directly with Gemini / Google"),
                Pair("(text)", "search installed apps or searches web automatically")
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        modifier = Modifier.width(95.dp)
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

            // ABOUT
            Text(
                text = "ABOUT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Minimal OS Launcher v1.3.0\nCreated by MD. Zarif Azfar\nPure text-first minimalist experience with full automation capabilities.",
                fontSize = 13.sp,
                color = MinimalGray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // App Picker Dialog for Home Shortcuts
        if (showAppPickerModal) {
            Dialog(onDismissRequest = { showAppPickerModal = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MinimalCardBg)
                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SELECT HOME SHORTCUTS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalWhite
                            )
                            IconButton(onClick = { showAppPickerModal = false }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MinimalGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(installedApps) { app ->
                                val isFav = app.packageName in favoriteAppPackages
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isFav) MinimalDarkCardBg else MinimalCardBg)
                                        .clickable {
                                            val updated = if (isFav) {
                                                favoriteAppPackages - app.packageName
                                            } else {
                                                favoriteAppPackages + app.packageName
                                            }
                                            scope.launch { userPreferences.setFavoriteApps(updated) }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isFav) MinimalRed else MinimalDarkCardBg)
                                            .border(1.dp, if (isFav) MinimalRed else MinimalDarkGray, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isFav) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MinimalWhite,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = app.label,
                                        fontSize = 14.sp,
                                        color = MinimalWhite,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MinimalRed)
                                .clickable { showAppPickerModal = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "DONE (${favoriteAppPackages.size} selected)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalWhite
                            )
                        }
                    }
                }
            }
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
