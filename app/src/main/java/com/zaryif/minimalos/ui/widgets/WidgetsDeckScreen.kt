package com.zaryif.minimalos.ui.widgets

import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.RestrictionMode
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WidgetsDeckScreen(
    storageRepository: StorageRepository,
    executeCommandUseCase: ExecuteCommandUseCase,
    currentRestrictionMode: RestrictionMode,
    onSelectRestrictionMode: (RestrictionMode) -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val todos by storageRepository.todos.collectAsState()

    var newTodoText by remember { mutableStateOf("") }
    var newEventText by remember { mutableStateOf("") }

    // Pomodoro Timer State
    var pomodoroSeconds by remember { mutableIntStateOf(25 * 60) }
    var isPomodoroRunning by remember { mutableStateOf(false) }
    var isBreakMode by remember { mutableStateOf(false) }

    LaunchedEffect(isPomodoroRunning) {
        while (isPomodoroRunning && pomodoroSeconds > 0) {
            delay(1000)
            pomodoroSeconds--
            if (pomodoroSeconds == 0) {
                isBreakMode = !isBreakMode
                pomodoroSeconds = if (isBreakMode) 5 * 60 else 25 * 60
                isPomodoroRunning = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val (x, y) = dragAmount
                    // Swipe right -> Return to Home
                    if (x > 30 && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                        onBackToHome()
                    }
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "rabbit deck",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite
                    )
                    Text(
                        text = "interactive cards · focus & automation",
                        fontSize = 12.sp,
                        color = MinimalGray
                    )
                }

                Text(
                    text = "HOME ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    modifier = Modifier.clickable { onBackToHome() }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Scrollable Deck of Interactive Cards
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // CARD 1: Interactive Pomodoro Focus Card (Rabbit style)
                item {
                    RabbitPomodoroCard(
                        seconds = pomodoroSeconds,
                        isRunning = isPomodoroRunning,
                        isBreak = isBreakMode,
                        onToggle = { isPomodoroRunning = !isPomodoroRunning },
                        onReset = {
                            isPomodoroRunning = false
                            pomodoroSeconds = if (isBreakMode) 5 * 60 else 25 * 60
                        },
                        onSwitchMode = { breakMode ->
                            isPomodoroRunning = false
                            isBreakMode = breakMode
                            pomodoroSeconds = if (breakMode) 5 * 60 else 25 * 60
                        }
                    )
                }

                // CARD 2: Quick Silent System Timers (One-tap execution)
                item {
                    RabbitQuickTimersCard(
                        onSetTimer = { seconds, label ->
                            executeCommandUseCase.execute("+${seconds}s")
                        }
                    )
                }

                // CARD 3: Interactive To-Dos Card with inline adding
                item {
                    RabbitTodoCard(
                        todos = todos.take(4),
                        newTodoText = newTodoText,
                        onNewTodoChange = { newTodoText = it },
                        onAddTodo = {
                            if (newTodoText.isNotBlank()) {
                                storageRepository.addTodo(newTodoText.trim())
                                newTodoText = ""
                            }
                        },
                        onToggleTodo = { id -> storageRepository.toggleTodo(id) }
                    )
                }

                // CARD 4: Quick Calendar Event Creation (Auto insert)
                item {
                    RabbitCalendarCard(
                        newEventText = newEventText,
                        onNewEventChange = { newEventText = it },
                        onAddEvent = {
                            if (newEventText.isNotBlank()) {
                                executeCommandUseCase.execute("*${newEventText.trim()}")
                                newEventText = ""
                            }
                        },
                        onOpenCalendar = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }

                // CARD 5: AI Brain Quick Prompts
                item {
                    RabbitAiPromptsCard(
                        onAskPrompt = { prompt ->
                            executeCommandUseCase.execute("?$prompt")
                        }
                    )
                }

                // CARD 6: Digital Diet / Restriction Mode Card
                item {
                    RabbitRestrictionsCard(
                        currentMode = currentRestrictionMode,
                        onSelectMode = onSelectRestrictionMode
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Bottom hint
            Text(
                text = "➔ SWIPE RIGHT FOR HOME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MinimalRed,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// -------------------------------------------------------------
// RABBIT POMODORO CARD
// -------------------------------------------------------------
@Composable
private fun RabbitPomodoroCard(
    seconds: Int,
    isRunning: Boolean,
    isBreak: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onSwitchMode: (Boolean) -> Unit
) {
    val mins = seconds / 60
    val secs = seconds % 60
    val timeFormatted = "%02d:%02d".format(mins, secs)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MinimalCardBg)
            .border(1.5.dp, MinimalRed, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBreak) "BREAK TIME" else "DEEP WORK POMODORO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        letterSpacing = 1.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "25m focus",
                        fontSize = 11.sp,
                        fontWeight = if (!isBreak) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isBreak) MinimalWhite else MinimalGray,
                        modifier = Modifier.clickable { onSwitchMode(false) }
                    )
                    Text(
                        text = "5m rest",
                        fontSize = 11.sp,
                        fontWeight = if (isBreak) FontWeight.Bold else FontWeight.Normal,
                        color = if (isBreak) MinimalWhite else MinimalGray,
                        modifier = Modifier.clickable { onSwitchMode(true) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    color = MinimalWhite
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MinimalDarkCardBg)
                            .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Reset",
                            tint = MinimalGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MinimalRed)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Start",
                            tint = MinimalWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RABBIT QUICK TIMERS CARD (One-Tap Silent Timers)
// -------------------------------------------------------------
@Composable
private fun RabbitQuickTimersCard(
    onSetTimer: (Int, String) -> Unit
) {
    val presets = listOf(
        Pair(300, "5m (power)"),
        Pair(900, "15m (reset)"),
        Pair(1800, "30m (sprint)"),
        Pair(2700, "45m (deep)")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MinimalRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ONE-TAP SYSTEM TIMERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (sec, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MinimalDarkCardBg)
                            .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                            .clickable { onSetTimer(sec, label) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.substringBefore(" "),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RABBIT TO-DO CARD
// -------------------------------------------------------------
@Composable
private fun RabbitTodoCard(
    todos: List<com.zaryif.minimalos.domain.model.Todo>,
    newTodoText: String,
    onNewTodoChange: (String) -> Unit,
    onAddTodo: () -> Unit,
    onToggleTodo: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FOCUS TASK STACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "${todos.count { !it.isCompleted }} pending",
                    fontSize = 11.sp,
                    color = MinimalGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inline input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MinimalDarkCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (newTodoText.isEmpty()) {
                        Text(
                            text = "add task directly…",
                            style = TextStyle(fontSize = 13.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = newTodoText,
                        onValueChange = onNewTodoChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onAddTodo() })
                    )
                }
                if (newTodoText.isNotEmpty()) {
                    IconButton(onClick = onAddTodo, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (todos.isEmpty()) {
                Text(
                    text = "No tasks in stack. Type above to add.",
                    fontSize = 12.sp,
                    color = MinimalGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                todos.forEach { todo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleTodo(todo.id) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (todo.isCompleted) MinimalRed else MinimalDarkCardBg)
                                .border(1.dp, if (todo.isCompleted) MinimalRed else MinimalDarkGray, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (todo.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MinimalWhite,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = todo.title,
                            fontSize = 13.sp,
                            color = if (todo.isCompleted) MinimalGray else MinimalWhite,
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RABBIT CALENDAR EVENT CARD (Auto silent create)
// -------------------------------------------------------------
@Composable
private fun RabbitCalendarCard(
    newEventText: String,
    onNewEventChange: (String) -> Unit,
    onAddEvent: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCHEDULE & EVENTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalRed,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "open calendar",
                    fontSize = 11.sp,
                    color = MinimalGray,
                    modifier = Modifier.clickable { onOpenCalendar() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = todayFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MinimalWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Inline Event Add (auto inserts into Google Calendar without needing "done" click)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MinimalDarkCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (newEventText.isEmpty()) {
                        Text(
                            text = "quick add event directly…",
                            style = TextStyle(fontSize = 13.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = newEventText,
                        onValueChange = onNewEventChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onAddEvent() })
                    )
                }
                if (newEventText.isNotEmpty()) {
                    IconButton(onClick = onAddEvent, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Create",
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RABBIT AI PROMPTS CARD
// -------------------------------------------------------------
@Composable
private fun RabbitAiPromptsCard(
    onAskPrompt: (String) -> Unit
) {
    val quickPrompts = listOf(
        "Summarize my day and prioritize top 3 outcomes",
        "Give me a deep focus quote for programming",
        "Explain quantum computing in 2 simple sentences"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MinimalRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI BRAIN SHORTCUTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            quickPrompts.forEach { prompt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAskPrompt(prompt) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        color = MinimalWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ask",
                        fontSize = 11.sp,
                        color = MinimalRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// RABBIT RESTRICTIONS CARD
// -------------------------------------------------------------
@Composable
private fun RabbitRestrictionsCard(
    currentMode: RestrictionMode,
    onSelectMode: (RestrictionMode) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MinimalRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACTIVE RESTRICTION MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(RestrictionMode.MONK, RestrictionMode.FOCUS, RestrictionMode.UNRESTRICTED).forEach { mode ->
                    val isSelected = mode == currentMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                            .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                            .clickable { onSelectMode(mode) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.title.split(" ").first(),
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
