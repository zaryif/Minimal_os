package com.zaryif.minimalos.ui.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.domain.model.Note
import com.zaryif.minimalos.domain.model.Todo
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@Composable
fun MinimalDynamicHomeWidgets(
    widgetsOrder: List<String>,
    todos: List<Todo>,
    notes: List<Note>,
    favoriteApps: List<AppInfo>,
    homeSystemWidgetIds: Set<Int>,
    appWidgetManager: AppWidgetManager?,
    appWidgetHost: AppWidgetHost?,
    showAllTodosPreference: Boolean = false,
    showAllNotesPreference: Boolean = false,
    onToggleTodo: (String) -> Unit,
    onOpenMemo: () -> Unit,
    onOpenCalendar: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onAddHomeWidget: (() -> Unit)?,
    onDeleteHomeWidget: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        widgetsOrder.forEach { widgetKey ->
            when (widgetKey) {
                "TODOS" -> {
                    MinimalHomeTodosWidget(
                        todos = todos,
                        initialShowAll = showAllTodosPreference,
                        onToggleTodo = onToggleTodo,
                        onOpenMemo = onOpenMemo
                    )
                }

                "NOTES" -> {
                    MinimalHomeNotesWidget(
                        notes = notes,
                        initialShowAll = showAllNotesPreference,
                        onOpenMemo = onOpenMemo
                    )
                }

                "CALENDAR" -> {
                    MinimalHomeCalendarWidget(
                        onOpenCalendar = onOpenCalendar
                    )
                }

                "SHORTCUTS" -> {
                    if (favoriteApps.isNotEmpty()) {
                        MinimalHomeShortcutsWidget(
                            favoriteApps = favoriteApps,
                            onLaunchApp = onLaunchApp
                        )
                    }
                }

                "SYSTEM_WIDGETS" -> {
                    if (appWidgetManager != null && appWidgetHost != null) {
                        MinimalHomeSystemWidgetsSection(
                            systemWidgetIds = homeSystemWidgetIds,
                            appWidgetManager = appWidgetManager,
                            appWidgetHost = appWidgetHost,
                            onAddWidget = onAddHomeWidget ?: {},
                            onDeleteWidget = onDeleteHomeWidget ?: {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalHomeTodosWidget(
    todos: List<Todo>,
    initialShowAll: Boolean = false,
    onToggleTodo: (String) -> Unit,
    onOpenMemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTodos = todos.filter { !it.isCompleted }
    if (activeTodos.isNotEmpty()) {
        var isExpanded by remember(initialShowAll) { mutableStateOf(initialShowAll) }
        val displayTodos = if (isExpanded) activeTodos else activeTodos.take(2)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MinimalCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                .padding(14.dp)
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
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "to-do",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (activeTodos.size > 2) {
                            Text(
                                text = if (isExpanded) "collapse" else "all (${activeTodos.size})",
                                fontSize = 11.sp,
                                color = MinimalRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { isExpanded = !isExpanded }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = "open memo",
                            fontSize = 11.sp,
                            color = MinimalGray,
                            modifier = Modifier.clickable { onOpenMemo() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                displayTodos.forEach { todo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleTodo(todo.id) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {}
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = todo.title,
                            fontSize = 13.sp,
                            color = MinimalWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalHomeNotesWidget(
    notes: List<Note>,
    initialShowAll: Boolean = false,
    onOpenMemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (notes.isNotEmpty()) {
        var isExpanded by remember(initialShowAll) { mutableStateOf(initialShowAll) }
        val displayNotes = if (isExpanded) notes else notes.take(2)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MinimalCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MinimalRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "notes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (notes.size > 2) {
                            Text(
                                text = if (isExpanded) "collapse" else "all (${notes.size})",
                                fontSize = 11.sp,
                                color = MinimalRed,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { isExpanded = !isExpanded }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = "open memo",
                            fontSize = 11.sp,
                            color = MinimalGray,
                            modifier = Modifier.clickable { onOpenMemo() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                displayNotes.forEach { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMemo() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            color = MinimalRed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = note.content,
                            fontSize = 13.sp,
                            color = MinimalWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalHomeCalendarWidget(
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MinimalCardBg)
            .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
            .clickable { onOpenCalendar() }
            .padding(12.dp)
    ) {
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
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "calendar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "view schedule & events",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = MinimalGray
                    )
                }
            }
            Text(
                text = "OPEN ➔",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed
            )
        }
    }
}

@Composable
fun MinimalHomeShortcutsWidget(
    favoriteApps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        favoriteApps.take(4).forEach { app ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MinimalDarkCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                    .clickable { onLaunchApp(app) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = app.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MinimalWhite
                )
            }
        }
    }
}

@Composable
fun MinimalHomeSystemWidgetsSection(
    systemWidgetIds: Set<Int>,
    appWidgetManager: AppWidgetManager,
    appWidgetHost: AppWidgetHost,
    onAddWidget: () -> Unit,
    onDeleteWidget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "widgets",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalWhite,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MinimalDarkCardBg)
                    .clickable { onAddWidget() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MinimalRed,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ADD WIDGET",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite
                )
            }
        }

        if (systemWidgetIds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .clickable { onAddWidget() }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Add system app widget (Spotify, Weather, Calendar)",
                    fontSize = 12.sp,
                    color = MinimalGray
                )
            }
        } else {
            systemWidgetIds.forEach { widgetId ->
                MinimalSystemWidgetHostItem(
                    widgetId = widgetId,
                    appWidgetManager = appWidgetManager,
                    appWidgetHost = appWidgetHost,
                    onDelete = { onDeleteWidget(widgetId) }
                )
            }
        }
    }
}
