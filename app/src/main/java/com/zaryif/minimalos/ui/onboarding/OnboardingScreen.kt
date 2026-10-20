package com.zaryif.minimalos.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.RestrictionMode
import com.zaryif.minimalos.ui.components.ButtonStyle
import com.zaryif.minimalos.ui.components.MinimalButton
import com.zaryif.minimalos.ui.components.MinimalCard
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@Composable
fun OnboardingScreen(
    onFinish: (RestrictionMode) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedMode by remember { mutableStateOf(RestrictionMode.UNRESTRICTED) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
                } else {
                    slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
                }
            },
            label = "OnboardingSteps"
        ) { step ->
            when (step) {
                0 -> StepYourApps(onNext = { currentStep = 1 })
                1 -> StepMinimalAsk(
                    onNext = { currentStep = 2 },
                    onSkip = { currentStep = 2 }
                )
                2 -> StepTypeToDoThings(onNext = { currentStep = 3 })
                3 -> StepHub(onNext = { currentStep = 4 })
                4 -> StepRestrictions(
                    selectedMode = selectedMode,
                    onSelectMode = { selectedMode = it },
                    onFinish = { onFinish(selectedMode) }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Your Apps (Screenshot 1)
// -------------------------------------------------------------
@Composable
private fun StepYourApps(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "your apps",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "swipe up from anywhere on home for the full list.\nstart typing instead and you get the command bar,\nwhich searches the same apps.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        MinimalCard(
            title = "Minimal Settings",
            icon = Icons.Default.Settings,
            iconColor = MinimalRed
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalCard(
            title = "Minimal Ask",
            icon = Icons.Default.ChatBubbleOutline,
            iconColor = MinimalRed
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalCard(
            title = "Minimal Clock",
            icon = Icons.Default.Schedule,
            iconColor = MinimalRed
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalCard(
            title = "Minimal Memo",
            icon = Icons.Default.EditNote,
            iconColor = MinimalRed
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "next",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 2: Minimal Ask (Screenshot 2)
// -------------------------------------------------------------
@Composable
private fun StepMinimalAsk(
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Minimal Ask",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "your helper to get things done, without distraction.",
            fontSize = 13.sp,
            color = MinimalGray
        )

        Spacer(modifier = Modifier.height(28.dp))

        MinimalCard(
            title = "ask about your day",
            subtitle = "what's on, when something is, who someone is"
        )
        Spacer(modifier = Modifier.height(12.dp))
        MinimalCard(
            title = "type ? from home",
            subtitle = "an answer without opening anything"
        )
        Spacer(modifier = Modifier.height(12.dp))
        MinimalCard(
            title = "it only sees what you allow",
            subtitle = "every source is a switch you control"
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "next",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalButton(
            text = "no thanks",
            onClick = onSkip,
            style = ButtonStyle.SECONDARY
        )
    }
}

// -------------------------------------------------------------
// STEP 3: Type to do things (Screenshot 3)
// -------------------------------------------------------------
@Composable
private fun StepTypeToDoThings(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "type to do things",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "from home, a symbol and a line is the whole job.\nnothing to open, nowhere to navigate.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // 2-Column Symbols Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                SymbolRow(symbol = "@", name = "message")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = "-", name = "to-do")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = "*", name = "event")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = ":", name = "alarm")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                SymbolRow(symbol = "#", name = "call")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = "!", name = "note")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = "+", name = "timer")
                Spacer(modifier = Modifier.height(20.dp))
                SymbolRow(symbol = "?", name = "ask")
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "a line with no symbol searches apps. the symbols\nthemselves are yours to remap in settings.",
            fontSize = 12.sp,
            color = MinimalGray,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "next",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

@Composable
private fun SymbolRow(symbol: String, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = symbol,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalRed,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
    }
}

// -------------------------------------------------------------
// STEP 4: Hub (Screenshot 4)
// -------------------------------------------------------------
@Composable
private fun StepHub(onNext: () -> Unit) {
    var selectedTab by remember { mutableStateOf("ALL") }
    val tabs = listOf("ALL", "MESSAGE", "CALLS", "EMAIL", "APPS", "FLAGGED")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "hub",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "messages, calls, mail and app alerts in one list, so\nthere's one place to look rather than nine.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Text(
                    text = tab,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MinimalWhite else MinimalGray,
                    modifier = Modifier.clickable { selectedTab = tab }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Cards
        MinimalCard(
            title = "jason m.",
            subtitle = "on my way",
            icon = Icons.Default.ChatBubbleOutline,
            trailingText = "now"
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalCard(
            title = "mum",
            subtitle = "missed call",
            icon = Icons.Default.Phone,
            trailingText = "5m"
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalCard(
            title = "amara w.",
            subtitle = "lunch thursday?",
            icon = Icons.Default.Email,
            trailingText = "1h"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ONE SWIPE RIGHT FROM HOME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MinimalRed
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "done",
            onClick = onNext,
            style = ButtonStyle.PRIMARY
        )
    }
}

// -------------------------------------------------------------
// STEP 5: Restrictions (Screenshot 5)
// -------------------------------------------------------------
@Composable
private fun StepRestrictions(
    selectedMode: RestrictionMode,
    onSelectMode: (RestrictionMode) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "restrictions",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MinimalWhite
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "the phone can hold you to a mode. set one up now,\nor later in settings.",
            fontSize = 13.sp,
            color = MinimalGray,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        MinimalCard(
            title = "monk mode",
            subtitle = "essentials only, grayscale always",
            isSelected = selectedMode == RestrictionMode.MONK,
            onClick = { onSelectMode(RestrictionMode.MONK) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        MinimalCard(
            title = "focus",
            subtitle = "everything, distracting apps on a clock",
            isSelected = selectedMode == RestrictionMode.FOCUS,
            onClick = { onSelectMode(RestrictionMode.FOCUS) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        MinimalCard(
            title = "unrestricted",
            subtitle = "everything, no guardrails",
            isSelected = selectedMode == RestrictionMode.UNRESTRICTED,
            onClick = { onSelectMode(RestrictionMode.UNRESTRICTED) }
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        MinimalButton(
            text = "setup",
            onClick = onFinish,
            style = ButtonStyle.PRIMARY
        )
        Spacer(modifier = Modifier.height(10.dp))
        MinimalButton(
            text = "continue later",
            onClick = onFinish,
            style = ButtonStyle.SECONDARY
        )
    }
}
