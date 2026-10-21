package com.zaryif.minimalos.ui.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.provider.CalendarContract
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.ui.viewinterop.AndroidView
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.Note
import com.zaryif.minimalos.domain.model.Todo
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.domain.timer.PomodoroTimerManager
import com.zaryif.minimalos.ui.components.CustomPomodoroDurationDialog
import com.zaryif.minimalos.ui.theme.AppleSanFranciscoFontFamily
import com.zaryif.minimalos.ui.theme.MinimalWhite
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WidgetsDeckScreen(
    storageRepository: StorageRepository,
    userPreferences: UserPreferences,
    executeCommandUseCase: ExecuteCommandUseCase,
    systemWidgetIds: Set<Int>,
    appWidgetManager: AppWidgetManager,
    appWidgetHost: AppWidgetHost,
    onAddSystemWidget: () -> Unit,
    onDeleteSystemWidget: (Int) -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val todos by storageRepository.todos.collectAsState()
    val notes by storageRepository.notes.collectAsState()
    val deckWidgetsOrder by userPreferences.deckWidgetsOrder.collectAsState(initial = UserPreferences.DEFAULT_DECK_WIDGETS)

    var newTodoText by remember { mutableStateOf("") }
    var newNoteText by remember { mutableStateOf("") }
    var newEventText by remember { mutableStateOf("") }

    // Global Pomodoro Timer State
    val pomodoroSeconds by PomodoroTimerManager.remainingSeconds.collectAsState()
    val isPomodoroRunning by PomodoroTimerManager.isRunning.collectAsState()
    val isBreakMode by PomodoroTimerManager.isBreakMode.collectAsState()
    val pomodoroWorkDuration by PomodoroTimerManager.workDurationSeconds.collectAsState()
    var showCustomPomodoroDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("") }

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
            .padding(horizontal = 20.dp, vertical = 14.dp)
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
                        text = "widgets",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite
                    )
                    Text(
                        text = "interactive cards · notes · widgets · focus",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Deck of Interactive Cards (Reorderable via Settings)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                deckWidgetsOrder.forEach { cardType ->
                    when (cardType) {
                        "NOTES" -> {
                            item(key = "NOTES") {
                                RabbitNotesCard(
                                    notes = notes.take(4),
                                    newNoteText = newNoteText,
                                    onNewNoteChange = { newNoteText = it },
                                    onAddNote = {
                                        if (newNoteText.isNotBlank()) {
                                            storageRepository.addNote(newNoteText.trim())
                                            newNoteText = ""
                                        }
                                    },
                                    onDeleteNote = { id -> storageRepository.deleteNote(id) }
                                )
                            }
                        }

                        "TODOS" -> {
                            item(key = "TODOS") {
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
                                    onToggleTodo = { id -> storageRepository.toggleTodo(id) },
                                    onDeleteTodo = { id -> storageRepository.deleteTodo(id) }
                                )
                            }
                        }

                        "CALENDAR" -> {
                            item(key = "CALENDAR") {
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
                        }

                        "POMODORO" -> {
                            item(key = "POMODORO") {
                                RabbitPomodoroCard(
                                    seconds = pomodoroSeconds,
                                    isRunning = isPomodoroRunning,
                                    isBreak = isBreakMode,
                                    workDuration = pomodoroWorkDuration,
                                    onToggle = { PomodoroTimerManager.toggle() },
                                    onReset = { PomodoroTimerManager.reset() },
                                    onSwitchMode = { breakMode -> PomodoroTimerManager.setBreakMode(breakMode) },
                                    onSetDuration = { mins -> PomodoroTimerManager.setWorkMinutes(mins) },
                                    onOpenCustomDialog = {
                                        customMinutesInput = (pomodoroWorkDuration / 60).toString()
                                        showCustomPomodoroDialog = true
                                    }
                                )
                            }
                        }

                        "TIMERS" -> {
                            item(key = "TIMERS") {
                                RabbitQuickTimersCard(
                                    onSetTimer = { seconds, _ ->
                                        executeCommandUseCase.execute("+${seconds}s")
                                    }
                                )
                            }
                        }

                        "SYSTEM_WIDGETS" -> {
                            item(key = "SYSTEM_WIDGETS_HEADER") {
                                RabbitSystemWidgetsHeader(onAddWidget = onAddSystemWidget)
                            }

                            if (systemWidgetIds.isEmpty()) {
                                item(key = "SYSTEM_WIDGETS_EMPTY") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(MinimalCardBg)
                                            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No external app widgets added yet.\nTap '+ ADD WIDGET' to host Calendar, Spotify, Weather, etc.",
                                            fontSize = 12.sp,
                                            color = MinimalGray,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            } else {
                                items(systemWidgetIds.toList(), key = { "widget_$it" }) { widgetId ->
                                    RabbitSystemWidgetHostItem(
                                        widgetId = widgetId,
                                        appWidgetManager = appWidgetManager,
                                        appWidgetHost = appWidgetHost,
                                        onDelete = { onDeleteSystemWidget(widgetId) }
                                    )
                                }
                            }
                        }
                    }
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

        if (showCustomPomodoroDialog) {
            CustomPomodoroDurationDialog(
                currentMinutes = pomodoroWorkDuration / 60,
                onDismiss = { showCustomPomodoroDialog = false },
                onSave = { mins ->
                    PomodoroTimerManager.setWorkMinutes(mins)
                    showCustomPomodoroDialog = false
                }
            )
        }
    }
}

// -------------------------------------------------------------
// RABBIT NOTES CARD
// -------------------------------------------------------------
@Composable
private fun RabbitNotesCard(
    notes: List<Note>,
    newNoteText: String,
    onNewNoteChange: (String) -> Unit,
    onAddNote: () -> Unit,
    onDeleteNote: (String) -> Unit
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
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "notes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "${notes.size} saved",
                    fontSize = 11.sp,
                    color = MinimalGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inline Note Input
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
                    if (newNoteText.isEmpty()) {
                        Text(
                            text = "quick note…",
                            style = TextStyle(fontSize = 13.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = newNoteText,
                        onValueChange = onNewNoteChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onAddNote() })
                    )
                }
                if (newNoteText.isNotEmpty()) {
                    IconButton(onClick = onAddNote, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Save",
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (notes.isEmpty()) {
                Text(
                    text = "No notes saved. Type above to write a note.",
                    fontSize = 12.sp,
                    color = MinimalGray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                notes.forEach { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            color = MinimalRed,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = note.content,
                            fontSize = 13.sp,
                            color = MinimalWhite,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onDeleteNote(note.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = MinimalGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
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
    todos: List<Todo>,
    newTodoText: String,
    onNewTodoChange: (String) -> Unit,
    onAddTodo: () -> Unit,
    onToggleTodo: (String) -> Unit,
    onDeleteTodo: (String) -> Unit
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
                        text = "to-dos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite,
                        letterSpacing = 0.5.sp
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
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (todo.isCompleted) MinimalRed else MinimalDarkCardBg)
                                .border(1.dp, if (todo.isCompleted) MinimalRed else MinimalDarkGray, RoundedCornerShape(4.dp))
                                .clickable { onToggleTodo(todo.id) },
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
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onToggleTodo(todo.id) }
                        )
                        IconButton(
                            onClick = { onDeleteTodo(todo.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MinimalGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
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
                        text = "schedule",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite,
                        letterSpacing = 0.5.sp
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

            // Inline Event Add (auto inserts into Calendar)
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
// RABBIT POMODORO CARD
// -------------------------------------------------------------
@Composable
private fun RabbitPomodoroCard(
    seconds: Int,
    isRunning: Boolean,
    isBreak: Boolean,
    workDuration: Int,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onSwitchMode: (Boolean) -> Unit,
    onSetDuration: (Int) -> Unit,
    onOpenCustomDialog: () -> Unit
) {
    val mins = seconds / 60
    val secs = seconds % 60
    val timeFormatted = "%02d:%02d".format(mins, secs)
    val workMins = workDuration / 60

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
                        text = if (isBreak) "break" else "pomodoro",
                        fontFamily = AppleSanFranciscoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${workMins}m focus",
                        fontFamily = AppleSanFranciscoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (!isBreak) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isBreak) MinimalWhite else MinimalGray,
                        modifier = Modifier.clickable { onSwitchMode(false) }
                    )
                    Text(
                        text = "5m rest",
                        fontFamily = AppleSanFranciscoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isBreak) FontWeight.Bold else FontWeight.Normal,
                        color = if (isBreak) MinimalWhite else MinimalGray,
                        modifier = Modifier.clickable { onSwitchMode(true) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Duration selector chips: 15m, 25m, 45m, 60m, custom
            if (!isBreak) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 25, 45, 60).forEach { presetMins ->
                        val isCurrent = workMins == presetMins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCurrent) MinimalRed else MinimalDarkCardBg)
                                .border(1.dp, if (isCurrent) MinimalRed else MinimalDarkGray, RoundedCornerShape(6.dp))
                                .clickable { onSetDuration(presetMins) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${presetMins}m",
                                fontFamily = AppleSanFranciscoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = MinimalWhite
                            )
                        }
                    }

                    val isCustom = workMins !in listOf(15, 25, 45, 60)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCustom) MinimalRed else MinimalDarkCardBg)
                            .border(1.dp, if (isCustom) MinimalRed else MinimalDarkGray, RoundedCornerShape(6.dp))
                            .clickable { onOpenCustomDialog() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isCustom) "${workMins}m custom" else "custom...",
                            fontFamily = AppleSanFranciscoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Normal,
                            color = MinimalWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormatted,
                    fontFamily = AppleSanFranciscoFontFamily,
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
        Pair(300, "5m"),
        Pair(900, "15m"),
        Pair(1800, "30m"),
        Pair(2700, "45m")
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
                    text = "timers",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite,
                    letterSpacing = 0.5.sp
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
                            text = label,
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
// SYSTEM WIDGETS HEADER
// -------------------------------------------------------------
@Composable
private fun RabbitSystemWidgetsHeader(onAddWidget: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = null,
                tint = MinimalRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "widgets",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalWhite,
                letterSpacing = 0.5.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MinimalRed)
                .clickable { onAddWidget() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "+ ADD WIDGET",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalWhite
            )
        }
    }
}

// -------------------------------------------------------------
// SYSTEM WIDGET HOST ITEM
// -------------------------------------------------------------
@Composable
private fun RabbitSystemWidgetHostItem(
    widgetId: Int,
    appWidgetManager: AppWidgetManager,
    appWidgetHost: AppWidgetHost,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val info = remember(widgetId) { appWidgetManager.getAppWidgetInfo(widgetId) }
                Text(
                    text = info?.label ?: "App Widget",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MinimalGray
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(22.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Widget",
                        tint = MinimalGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 240.dp),
                factory = { ctx ->
                    val info = appWidgetManager.getAppWidgetInfo(widgetId)
                    if (info != null) {
                        try {
                            appWidgetHost.createView(ctx, widgetId, info).apply {
                                setAppWidget(widgetId, info)
                            }
                        } catch (_: Exception) {
                            android.view.View(ctx)
                        }
                    } else {
                        android.view.View(ctx)
                    }
                }
            )
        }
    }
}
