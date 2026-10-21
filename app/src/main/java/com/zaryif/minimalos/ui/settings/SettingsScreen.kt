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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zaryif.minimalos.data.preferences.AiProvider
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.ui.theme.AppleSanFranciscoFontFamily
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalThemeRegistry
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun SettingsScreen(
    installedApps: List<AppInfo>,
    userPreferences: UserPreferences,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedAiProvider by userPreferences.aiProvider.collectAsState(initial = AiProvider.GEMINI)
    val assistantPackage by userPreferences.assistantPackage.collectAsState(initial = null)
    val showCommandFeedback by userPreferences.showCommandFeedback.collectAsState(initial = true)
    val customWallpaperUri by userPreferences.customWallpaperUri.collectAsState(initial = null)
    val favoriteAppPackages by userPreferences.favoriteApps.collectAsState(initial = emptySet())
    val currentThemeId by userPreferences.themeId.collectAsState(initial = "AMOLED_RED")
    val homeWidgets by userPreferences.homeWidgets.collectAsState(initial = UserPreferences.DEFAULT_HOME_WIDGETS)
    val deckWidgetsOrder by userPreferences.deckWidgetsOrder.collectAsState(initial = UserPreferences.DEFAULT_DECK_WIDGETS)
    val customCommands by userPreferences.customCommands.collectAsState(initial = emptyMap())
    val showAllTodosPref by userPreferences.homeShowAllTodos.collectAsState(initial = false)
    val showAllNotesPref by userPreferences.homeShowAllNotes.collectAsState(initial = false)

    var showAppPickerModal by remember { mutableStateOf(false) }
    var showAddCommandModal by remember { mutableStateOf(false) }

    // Photo Gallery Wallpaper Picker with persistent local storage copying
    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val file = File(context.filesDir, "custom_wallpaper_${System.currentTimeMillis()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    userPreferences.setCustomWallpaperUri(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 14.dp)
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

            // SECTION 1: THEMES & ACCENTS
            Text(
                text = "THEMES & ACCENTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MinimalThemeRegistry.THEMES.forEach { theme ->
                    val isSelected = theme.id == currentThemeId
                    Box(
                        modifier = Modifier
                            .width(135.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(theme.card)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) theme.accent else theme.border,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                scope.launch { userPreferences.setThemeId(theme.id) }
                            }
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(theme.accent)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = theme.accent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = theme.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 2: CUSTOM COMMANDS & ALIASES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM COMMANDS & ALIASES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MinimalRed)
                        .clickable { showAddCommandModal = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+ ADD ALIAS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (customCommands.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MinimalCardBg)
                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                        .clickable { showAddCommandModal = true }
                        .padding(14.dp)
                ) {
                    Text(
                        text = "No custom commands yet. Tap '+ ADD ALIAS' to create shortcuts like 'yt' ➔ YouTube or 'wifi' ➔ Settings.",
                        fontSize = 12.sp,
                        color = MinimalGray,
                        lineHeight = 18.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MinimalCardBg)
                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    customCommands.forEach { (keyword, target) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = keyword,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalRed,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "➔ $target",
                                    fontSize = 11.sp,
                                    color = MinimalGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            IconButton(
                                onClick = { scope.launch { userPreferences.removeCustomCommand(keyword) } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MinimalGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 3: HOME SCREEN WIDGETS CUSTOMIZATION
            Text(
                text = "HOME SCREEN WIDGETS & ORDER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Display preference toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "show all to-dos on home", fontSize = 13.sp, color = MinimalWhite)
                    Text(text = "display complete task list instead of top 2", fontSize = 11.sp, color = MinimalGray)
                }
                Switch(
                    checked = showAllTodosPref,
                    onCheckedChange = { scope.launch { userPreferences.setHomeShowAllTodos(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = MinimalWhite, checkedTrackColor = MinimalRed)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "show all notes on home", fontSize = 13.sp, color = MinimalWhite)
                    Text(text = "display all memo entries instead of recent 2", fontSize = 11.sp, color = MinimalGray)
                }
                Switch(
                    checked = showAllNotesPref,
                    onCheckedChange = { scope.launch { userPreferences.setHomeShowAllNotes(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = MinimalWhite, checkedTrackColor = MinimalRed)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Available Home Widgets
            val allHomeWidgetOptions = listOf(
                Pair("TODOS", "to-dos (active focus tasks)"),
                Pair("NOTES", "notes (recent memos)"),
                Pair("CALENDAR", "calendar (glance & schedule)"),
                Pair("SHORTCUTS", "shortcuts (favorite pinned apps)"),
                Pair("SYSTEM_WIDGETS", "widgets (native Android app widgets)")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                homeWidgets.forEachIndexed { index, widgetKey ->
                    val label = allHomeWidgetOptions.find { it.first == widgetKey }?.second ?: widgetKey
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MinimalDarkCardBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (index > 0) {
                                IconButton(
                                    onClick = {
                                        val mutable = homeWidgets.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index - 1]
                                        mutable[index - 1] = temp
                                        scope.launch { userPreferences.setHomeWidgets(mutable) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = MinimalRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            if (index < homeWidgets.size - 1) {
                                IconButton(
                                    onClick = {
                                        val mutable = homeWidgets.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index + 1]
                                        mutable[index + 1] = temp
                                        scope.launch { userPreferences.setHomeWidgets(mutable) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = MinimalRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(
                                onClick = {
                                    val updated = homeWidgets - widgetKey
                                    scope.launch { userPreferences.setHomeWidgets(updated) }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = MinimalGray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Add disabled widgets button
                val unadded = allHomeWidgetOptions.filter { it.first !in homeWidgets }
                if (unadded.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        unadded.forEach { (key, name) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MinimalDarkCardBg)
                                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(6.dp))
                                    .clickable {
                                        scope.launch { userPreferences.setHomeWidgets(homeWidgets + key) }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ ${name.split(" ").first()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 4: WIDGETS DECK REORDER & CUSTOMIZATION
            Text(
                text = "WIDGETS DECK REORDER & CARDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val allDeckWidgetOptions = listOf(
                Pair("NOTES", "notes card"),
                Pair("TODOS", "to-dos card"),
                Pair("CALENDAR", "schedule & events card"),
                Pair("POMODORO", "pomodoro focus card"),
                Pair("TIMERS", "quick timers card"),
                Pair("SYSTEM_WIDGETS", "system app widgets")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                deckWidgetsOrder.forEachIndexed { index, deckKey ->
                    val label = allDeckWidgetOptions.find { it.first == deckKey }?.second ?: deckKey
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MinimalDarkCardBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (index > 0) {
                                IconButton(
                                    onClick = {
                                        val mutable = deckWidgetsOrder.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index - 1]
                                        mutable[index - 1] = temp
                                        scope.launch { userPreferences.setDeckWidgetsOrder(mutable) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = MinimalRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            if (index < deckWidgetsOrder.size - 1) {
                                IconButton(
                                    onClick = {
                                        val mutable = deckWidgetsOrder.toMutableList()
                                        val temp = mutable[index]
                                        mutable[index] = mutable[index + 1]
                                        mutable[index + 1] = temp
                                        scope.launch { userPreferences.setDeckWidgetsOrder(mutable) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = MinimalRed, modifier = Modifier.size(16.dp))
                                }
                            }
                            IconButton(
                                onClick = {
                                    val updated = deckWidgetsOrder - deckKey
                                    scope.launch { userPreferences.setDeckWidgetsOrder(updated) }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = MinimalGray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Add disabled deck widgets
                val unaddedDeck = allDeckWidgetOptions.filter { it.first !in deckWidgetsOrder }
                if (unaddedDeck.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        unaddedDeck.forEach { (key, name) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MinimalDarkCardBg)
                                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(6.dp))
                                    .clickable {
                                        scope.launch { userPreferences.setDeckWidgetsOrder(deckWidgetsOrder + key) }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ ${name.split(" ").first()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MinimalRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 5: HOME SCREEN APP SHORTCUTS
            Text(
                text = "HOME SCREEN APP SHORTCUTS",
                fontSize = 11.sp,
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

            // SECTION 6: DEFAULT ASSISTANT (Available & Allowed Apps)
            Text(
                text = "DEFAULT ASSISTANT",
                fontFamily = AppleSanFranciscoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Choose which available and allowed digital assistant handles assistant commands and quick triggers.",
                fontFamily = AppleSanFranciscoFontFamily,
                fontSize = 12.sp,
                color = MinimalGray,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Dynamically discover available & allowed assistants
            val availableAssistants = remember(context) {
                val pm = context.packageManager
                val list = mutableListOf<Triple<String, String, String>>() // id, name, tag

                // 1. System Default
                list.add(Triple("SYSTEM_DEFAULT", "System Default Assistant", "OS DEFAULT"))

                val addedPackages = mutableSetOf<String>()

                // 2. Apps explicitly registered with Android for ACTION_ASSIST
                try {
                    val assistIntent = Intent(Intent.ACTION_ASSIST)
                    val resolutions = pm.queryIntentActivities(assistIntent, 0)
                    for (res in resolutions) {
                        val pkg = res.activityInfo.packageName
                        if (pkg != context.packageName && !addedPackages.contains(pkg)) {
                            val label = res.loadLabel(pm).toString()
                            addedPackages.add(pkg)
                            list.add(Triple(pkg, label, "ALLOWED ASSIST"))
                        }
                    }
                } catch (_: Exception) {}

                // 3. Known installed AI assistants
                val knownAi = listOf(
                    "com.google.android.apps.bard" to "Google Gemini",
                    "com.google.android.googlequicksearchbox" to "Google Assistant",
                    "com.openai.chatgpt" to "ChatGPT",
                    "com.microsoft.copilot" to "Microsoft Copilot",
                    "com.anthropic.claude" to "Claude",
                    "ai.perplexity.app" to "Perplexity"
                )

                for ((pkg, name) in knownAi) {
                    if (!addedPackages.contains(pkg)) {
                        try {
                            val appInfo = pm.getApplicationInfo(pkg, 0)
                            if (appInfo.enabled) {
                                val label = pm.getApplicationLabel(appInfo).toString().ifBlank { name }
                                addedPackages.add(pkg)
                                list.add(Triple(pkg, label, "INSTALLED AI"))
                            }
                        } catch (_: Exception) {}
                    }
                }

                list
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
            ) {
                availableAssistants.forEachIndexed { index, (id, name, tag) ->
                    val isSelected = if (assistantPackage.isNullOrBlank() || assistantPackage == "SYSTEM_DEFAULT") {
                        id == "SYSTEM_DEFAULT"
                    } else {
                        id == assistantPackage
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    userPreferences.setAssistantPackage(id)
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = name,
                                    fontFamily = AppleSanFranciscoFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) MinimalWhite else MinimalGray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MinimalDarkCardBg)
                                        .border(0.5.dp, if (tag == "OS DEFAULT") MinimalRed else MinimalDarkGray, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontFamily = AppleSanFranciscoFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tag == "OS DEFAULT") MinimalRed else MinimalGray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (id) {
                                    "SYSTEM_DEFAULT" -> "Hands off to Android's default assistant with voice & overlay support"
                                    else -> "Directly launches $name for assistant actions"
                                },
                                fontFamily = AppleSanFranciscoFontFamily,
                                fontSize = 11.sp,
                                color = MinimalDarkGray
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MinimalRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MinimalWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (index < availableAssistants.size - 1) {
                        HorizontalDivider(color = MinimalDarkGray.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SettingsActionRow(
                title = "android assistant permissions",
                subtitle = "configure default digital assistant in Android OS settings",
                onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        try {
                            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 7: WALLPAPER & APPEARANCE
            Text(
                text = "WALLPAPER & APPEARANCE",
                fontFamily = AppleSanFranciscoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Curated Wallpaper Presets
            Text(
                text = "CURATED PRESETS",
                fontFamily = AppleSanFranciscoFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MinimalGray,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val wallpaperPresets = listOf(
                Pair("DEFAULT_AMOLED", "AMOLED Black"),
                Pair("DEFAULT_MIDNIGHT", "Midnight"),
                Pair("DEFAULT_SPACE_GRAY", "Space Gray"),
                Pair("DEFAULT_TITANIUM", "Titanium")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                wallpaperPresets.forEach { (presetKey, presetName) ->
                    val isSelected = customWallpaperUri == presetKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                            .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                            .clickable {
                                scope.launch { userPreferences.setCustomWallpaperUri(presetKey) }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = presetName,
                            fontFamily = AppleSanFranciscoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = MinimalWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SettingsActionRow(
                title = "choose wallpaper from photos",
                subtitle = if (customWallpaperUri != null && !customWallpaperUri!!.startsWith("DEFAULT_")) "custom photo active (tap to change)" else "select an image from your gallery",
                onClick = { wallpaperPickerLauncher.launch("image/*") }
            )

            if (customWallpaperUri != null) {
                HorizontalDivider(color = MinimalDarkGray, thickness = 0.5.dp)
                SettingsActionRow(
                    title = "reset to theme background",
                    subtitle = "restore default clean background",
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

            // SECTION 8: NOTIFICATIONS & FEEDBACK
            Text(
                text = "FEEDBACK & POPUPS",
                fontFamily = AppleSanFranciscoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .clickable {
                        scope.launch { userPreferences.setShowCommandFeedback(!showCommandFeedback) }
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "action confirmation popups",
                        fontFamily = AppleSanFranciscoFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MinimalWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "show transient 'Saved to-do' / 'Saved note' pill on home screen",
                        fontFamily = AppleSanFranciscoFontFamily,
                        fontSize = 11.sp,
                        color = MinimalGray
                    )
                }
                Switch(
                    checked = showCommandFeedback,
                    onCheckedChange = { checked ->
                        scope.launch { userPreferences.setShowCommandFeedback(checked) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MinimalWhite,
                        checkedTrackColor = MinimalRed,
                        uncheckedThumbColor = MinimalGray,
                        uncheckedTrackColor = MinimalDarkCardBg
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SECTION 8: SYSTEM CONFIGURATION
            Text(
                text = "SYSTEM CONFIGURATION",
                fontSize = 11.sp,
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

            Spacer(modifier = Modifier.height(32.dp))

            // ABOUT
            Text(
                text = "ABOUT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Minimal OS Launcher v1.4.0\nCreated by MD. Zarif Azfar\nApple San Francisco typography · Rabbit Deck · Multi-Theme engine.",
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
                                        color = MinimalWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Custom Command Dialog
        if (showAddCommandModal) {
            var newKeyword by remember { mutableStateOf("") }
            var newTarget by remember { mutableStateOf("") }

            Dialog(onDismissRequest = { showAddCommandModal = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MinimalBlack)
                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEW COMMAND ALIAS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalWhite
                            )
                            IconButton(onClick = { showAddCommandModal = false }, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MinimalGray, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "KEYWORD / TRIGGER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MinimalRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MinimalDarkCardBg)
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            if (newKeyword.isEmpty()) {
                                Text(text = "e.g. yt, gh, wifi, reddit", fontSize = 13.sp, color = MinimalGray)
                            }
                            BasicTextField(
                                value = newKeyword,
                                onValueChange = { newKeyword = it },
                                singleLine = true,
                                cursorBrush = SolidColor(MinimalRed),
                                textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite, fontFamily = FontFamily.Monospace),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(text = "ACTION / URL / SETTING / APP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MinimalRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MinimalDarkCardBg)
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            if (newTarget.isEmpty()) {
                                Text(text = "e.g. https://youtube.com", fontSize = 13.sp, color = MinimalGray)
                            }
                            BasicTextField(
                                value = newTarget,
                                onValueChange = { newTarget = it },
                                singleLine = true,
                                cursorBrush = SolidColor(MinimalRed),
                                textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite, fontFamily = FontFamily.Monospace),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick presets chips
                        Text(text = "QUICK TEMPLATES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MinimalGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val templates = listOf(
                                Pair("yt", "https://youtube.com"),
                                Pair("gh", "https://github.com"),
                                Pair("wifi", "android.settings.WIFI_SETTINGS"),
                                Pair("rd", "https://reddit.com")
                            )
                            templates.forEach { (kw, target) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MinimalDarkCardBg)
                                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(6.dp))
                                        .clickable {
                                            newKeyword = kw
                                            newTarget = target
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = kw, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MinimalRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (newKeyword.isNotBlank() && newTarget.isNotBlank()) MinimalRed else MinimalDarkGray)
                                .clickable(enabled = newKeyword.isNotBlank() && newTarget.isNotBlank()) {
                                    scope.launch {
                                        userPreferences.setCustomCommand(newKeyword.trim(), newTarget.trim())
                                        showAddCommandModal = false
                                    }
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "SAVE COMMAND", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MinimalWhite)
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
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
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
