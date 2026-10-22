package com.zaryif.minimalos.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.ui.components.ButtonStyle
import com.zaryif.minimalos.ui.components.MinimalButton
import com.zaryif.minimalos.ui.components.MinimalCard
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalOnAccent
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalThemeRegistry
import com.zaryif.minimalos.ui.theme.MinimalWhite
import com.zaryif.minimalos.ui.theme.currentActiveTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    userPreferences: UserPreferences? = null,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val prefs = userPreferences ?: remember { UserPreferences(context) }
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 5

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Navigation & Step Indicator Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { currentStep-- }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MinimalGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BACK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalGray,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Step Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        Box(
                            modifier = Modifier
                                .size(if (i == currentStep) 8.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (i == currentStep) MinimalRed else MinimalDarkGray)
                        )
                    }
                }

                // Step Counter Text
                Text(
                    text = "${currentStep + 1}/$totalSteps",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MinimalGray
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                        } else {
                            slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
                        }
                    },
                    label = "OnboardingContent"
                ) { step ->
                    when (step) {
                        0 -> StepWelcome(onNext = { currentStep = 1 })
                        1 -> StepCommandBar(onNext = { currentStep = 2 })
                        2 -> StepWidgetsDeck(onNext = { currentStep = 3 })
                        3 -> StepNotificationHub(onNext = { currentStep = 4 })
                        4 -> StepSetupWizard(
                            userPreferences = prefs,
                            onFinish = onFinish
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Welcome & Philosophy
// -------------------------------------------------------------
@Composable
private fun StepWelcome(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "welcome to\nminimal os",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "an intentional, text-first home launcher designed to reclaim your attention and time.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        FeatureHighlightCard(
            icon = Icons.Default.Terminal,
            title = "Type to Do Things",
            description = "A unified command line on your home screen. Set alarms, launch timers, delete apps, search YouTube, and ask AI in under 2 seconds."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.Widgets,
            title = "Live Widgets Deck",
            description = "High-contrast modular cards: live alarm countdowns, real-time Pomodoro timers, quick memos, and native Android widgets."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Unified Notification Hub",
            description = "One calm place for messages, calls, emails, and alerts. Swipe right from home to check everything without getting distracted."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.AutoAwesome,
            title = "100% Local & Zero Telemetry",
            description = "No tracking, no cloud accounts, no ads, and strictly zero emojis. Everything stays private on your device."
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "EXPLORE FEATURES ->",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 2: The Unified Command Box
// -------------------------------------------------------------
@Composable
private fun StepCommandBar(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "the command box",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "a unified terminal right on your home screen. type a keyword or symbol for instant action.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "NEW & POWER COMMANDS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalRed,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        CommandSampleCard(
            trigger = "uninstall chatgpt",
            description = "Uninstalls app directly via native system deletion dialog",
            symbol = "/"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CommandSampleCard(
            trigger = "assistant what is gravity",
            description = "Sends prompt directly to Gemini, ChatGPT, Claude, or Copilot",
            symbol = "?"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CommandSampleCard(
            trigger = "yt lofi beats",
            description = "Searches directly in the YouTube app with web fallback",
            symbol = "$"
        )
        Spacer(modifier = Modifier.height(8.dp))

        CommandSampleCard(
            trigger = "pomo 50m",
            description = "Parses time naturally and starts live home Pomodoro countdown",
            symbol = "%"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DAILY SHORTCUTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalRed,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                MiniSymbolItem("-", "to-do task", "- buy coffee")
                Spacer(modifier = Modifier.height(8.dp))
                MiniSymbolItem("@", "send sms", "@ Alex late 5m")
                Spacer(modifier = Modifier.height(8.dp))
                MiniSymbolItem(":", "set alarm", ": 7:30 AM")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                MiniSymbolItem("!", "quick note", "! project idea")
                Spacer(modifier = Modifier.height(8.dp))
                MiniSymbolItem("#", "quick call", "# John Doe")
                Spacer(modifier = Modifier.height(8.dp))
                MiniSymbolItem("+", "countdown", "+ 25m")
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Terminal Superpowers Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MinimalDarkCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TERMINAL HISTORY & MARKDOWN LOGGING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Cycle through previous commands anytime using the on-screen Up and Down arrows.\n" +
                            "• All commands are continuously logged to minimal_command_history.md.\n" +
                            "• Download your full history .md file at any time in Settings.",
                    fontSize = 11.sp,
                    color = MinimalGray,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "NEXT: WIDGETS DECK ->",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 3: Widgets Deck & Live Countdowns
// -------------------------------------------------------------
@Composable
private fun StepWidgetsDeck(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "widgets & deck",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "high-contrast utility cards and real-time live widgets right on your home screen.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureHighlightCard(
            icon = Icons.Default.Alarm,
            title = "Live Alarm Countdown Widget",
            description = "Pins directly to your home screen. Calculates the exact hours and minutes remaining until your next scheduled alarm rings."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.HourglassBottom,
            title = "Live Pomodoro Focus Widget",
            description = "Work and break cycles (15m, 25m, 45m, 60m) with live play, pause, and cycle progress controls on your home screen."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.EditNote,
            title = "Minimal Memo with Real-Time Search",
            description = "Quick scratchpad for thoughts and focus tasks. Features an instant search filter and inline add bar without opening new pages."
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.Widgets,
            title = "Native App Widget Host",
            description = "Embed standard Android widgets (Spotify, Keep, Weather, Banking) cleanly into the card system."
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Gesture Guide Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MinimalDarkCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "INTUITIVE GESTURE NAVIGATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "• Swipe Up from Home -> Full App Drawer with search", fontSize = 11.sp, color = MinimalWhite)
                Text(text = "• Swipe Right from Home -> Unified Notification Hub", fontSize = 11.sp, color = MinimalWhite)
                Text(text = "• Tap Header Icons -> Quick access to Memo, Clock, and Settings", fontSize = 11.sp, color = MinimalWhite)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "NEXT: NOTIFICATION HUB ->",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 4: Notification Hub
// -------------------------------------------------------------
@Composable
private fun StepNotificationHub(onNext: () -> Unit) {
    val tabs = listOf("ALL", "MESSAGE", "CALLS", "EMAIL", "APPS", "FLAGGED")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "notification hub",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "messages, calls, emails, and alerts aggregated into one calm, unified stream.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mock Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MinimalDarkCardBg)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEachIndexed { index, tab ->
                Text(
                    text = tab,
                    fontSize = 10.sp,
                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == 0) MinimalRed else MinimalGray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        MinimalCard(
            title = "jason m.",
            subtitle = "on my way now",
            icon = Icons.Default.ChatBubbleOutline,
            trailingText = "2m"
        )
        Spacer(modifier = Modifier.height(8.dp))

        MinimalCard(
            title = "sarah k.",
            subtitle = "missed call",
            icon = Icons.Default.Phone,
            trailingText = "15m"
        )
        Spacer(modifier = Modifier.height(8.dp))

        MinimalCard(
            title = "team sync",
            subtitle = "quarterly planning update",
            icon = Icons.Default.Email,
            trailingText = "1h"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MinimalDarkCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ONE SWIPE RIGHT FROM HOME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stop bouncing across multiple apps to check alerts. Filter by category with one tap and clear notifications with zero distraction.",
                    fontSize = 11.sp,
                    color = MinimalGray,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "SETUP WIZARD ->",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 5: Interactive Setup Wizard (Preferences)
// -------------------------------------------------------------
@Composable
private fun StepSetupWizard(
    userPreferences: UserPreferences,
    onFinish: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var selectedThemeId by remember { mutableStateOf("AMOLED_RED") }
    var enableAlarmCountdown by remember { mutableStateOf(true) }
    var enablePomodoroWidget by remember { mutableStateOf(true) }
    var enableTodosWidget by remember { mutableStateOf(true) }
    var enableNotesWidget by remember { mutableStateOf(false) }
    var enableCommandHistory by remember { mutableStateOf(true) }
    var selectedAssistant by remember { mutableStateOf("SYSTEM_DEFAULT") }

    val assistantOptions = listOf(
        Pair("SYSTEM_DEFAULT", "System Default / Auto"),
        Pair("com.google.android.apps.bard", "Google Gemini"),
        Pair("com.openai.chatgpt", "ChatGPT"),
        Pair("com.anthropic.claude", "Claude AI"),
        Pair("com.microsoft.copilot", "Copilot")
    )

    fun applyAndLaunch() {
        scope.launch {
            // 1. Theme
            userPreferences.setThemeId(selectedThemeId)

            // 2. Home Widgets
            val widgets = mutableListOf<String>()
            if (enableAlarmCountdown) widgets.add("ALARM_COUNTDOWN")
            if (enablePomodoroWidget) widgets.add("POMODORO")
            if (enableTodosWidget) widgets.add("TODOS")
            if (enableNotesWidget) widgets.add("NOTES")
            widgets.add("CALENDAR")
            widgets.add("SHORTCUTS")
            userPreferences.setHomeWidgets(widgets.distinct())

            // 3. Command History
            userPreferences.setCommandHistoryEnabled(enableCommandHistory)

            // 4. Assistant
            if (selectedAssistant == "SYSTEM_DEFAULT") {
                userPreferences.setAssistantPackage(null)
            } else {
                userPreferences.setAssistantPackage(selectedAssistant)
            }

            // 5. Complete Onboarding
            userPreferences.setOnboardingCompleted(true)

            onFinish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "setup wizard",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "personalize your starting experience now. you can always reconfigure these in Settings.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Choose Theme
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "1. CHOOSE YOUR PALETTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MinimalThemeRegistry.THEMES.forEach { theme ->
                val isSelected = selectedThemeId.equals(theme.id, ignoreCase = true)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MinimalCardBg else MinimalDarkCardBg)
                        .border(
                            1.dp,
                            if (isSelected) MinimalRed else MinimalDarkGray,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedThemeId = theme.id
                            currentActiveTheme = theme
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(theme.accent)
                                .border(1.dp, theme.border, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = theme.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MinimalWhite
                            )
                            Text(
                                text = "Accent ${theme.id.lowercase()}",
                                fontSize = 10.sp,
                                color = MinimalGray
                            )
                        }
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MinimalRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Home Screen Live Widgets
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "2. HOME SCREEN LIVE WIDGETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        WizardToggleRow(
            title = "Live Alarm Countdown",
            subtitle = "Displays exact countdown to next system alarm on home",
            checked = enableAlarmCountdown,
            onCheckedChange = { enableAlarmCountdown = it }
        )
        Spacer(modifier = Modifier.height(6.dp))

        WizardToggleRow(
            title = "Live Pomodoro Widget",
            subtitle = "Displays focus timer progress and live controls on home",
            checked = enablePomodoroWidget,
            onCheckedChange = { enablePomodoroWidget = it }
        )
        Spacer(modifier = Modifier.height(6.dp))

        WizardToggleRow(
            title = "Focus To-Dos Widget",
            subtitle = "Pins your top priority tasks directly on home screen",
            checked = enableTodosWidget,
            onCheckedChange = { enableTodosWidget = it }
        )
        Spacer(modifier = Modifier.height(6.dp))

        WizardToggleRow(
            title = "Recent Notes Widget",
            subtitle = "Pins your quick scratchpad thoughts on home screen",
            checked = enableNotesWidget,
            onCheckedChange = { enableNotesWidget = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Terminal Command History
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "3. TERMINAL HISTORY & EXPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        WizardToggleRow(
            title = "Enable Terminal Command History",
            subtitle = "Enables Up/Down arrow command recall and saves to minimal_command_history.md",
            checked = enableCommandHistory,
            onCheckedChange = { enableCommandHistory = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: Digital Assistant Preference
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "4. PREFERRED DIGITAL ASSISTANT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            assistantOptions.forEach { (pkg, label) ->
                val isSelected = selectedAssistant == pkg
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MinimalCardBg else MinimalDarkCardBg)
                        .border(
                            1.dp,
                            if (isSelected) MinimalRed else MinimalDarkGray,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedAssistant = pkg }
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = MinimalWhite
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MinimalRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        MinimalButton(
            text = "LAUNCH MINIMAL OS ->",
            onClick = { applyAndLaunch() },
            style = ButtonStyle.PRIMARY
        )
        Spacer(modifier = Modifier.height(10.dp))

        MinimalButton(
            text = "APPLY DEFAULTS & LAUNCH",
            onClick = {
                scope.launch {
                    userPreferences.setOnboardingCompleted(true)
                    onFinish()
                }
            },
            style = ButtonStyle.SECONDARY
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
private fun FeatureHighlightCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MinimalDarkCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MinimalRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MinimalWhite
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MinimalGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun CommandSampleCard(
    trigger: String,
    description: String,
    symbol: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trigger,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MinimalWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MinimalGray,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MinimalDarkCardBg)
                    .border(0.5.dp, MinimalRed, RoundedCornerShape(4.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = symbol,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed
                )
            }
        }
    }
}

@Composable
private fun MiniSymbolItem(
    symbol: String,
    label: String,
    example: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = symbol,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MinimalWhite
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = example,
                fontSize = 10.sp,
                color = MinimalGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun WizardToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MinimalDarkCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MinimalWhite
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MinimalGray,
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MinimalOnAccent,
                checkedTrackColor = MinimalRed,
                uncheckedThumbColor = MinimalGray,
                uncheckedTrackColor = MinimalDarkCardBg
            )
        )
    }
}
