package com.zaryif.minimalos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.Note
import com.zaryif.minimalos.domain.model.Todo
import com.zaryif.minimalos.domain.timer.PomodoroTimerManager
import com.zaryif.minimalos.ui.theme.AppleSanFranciscoFontFamily
import com.zaryif.minimalos.ui.theme.SlabInk
import com.zaryif.minimalos.ui.theme.SlabLilac
import com.zaryif.minimalos.ui.theme.SlabLime
import com.zaryif.minimalos.ui.theme.SlabOrange
import com.zaryif.minimalos.ui.theme.SlabPaper
import com.zaryif.minimalos.ui.theme.SlabSky
import java.util.Locale

/**
 * Lumen OS 2 Slab Accordion Stack Box.
 *
 * Implements the exact Lumen OS 2 design philosophy:
 * - Solid colour slabs that stack and overlap
 * - Giant numerals bleeding with high contrast
 * - Accordion drawers with spring fling physics (damping 0.75f, stiffness 400f)
 * - Tactile press contraction (0.98f scale)
 * - 2dp crisp solid borders and ink-on-color typography
 */
@Composable
fun LumenStackBox(
    todos: List<Todo>,
    notes: List<Note>,
    onToggleTodo: (String) -> Unit,
    onOpenMemo: () -> Unit,
    onOpenClock: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToHub: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: Watch (Tasks), 1: Make (Focus/Timer), 2: Play (Notes), 3: System (Deck)
    var openDrawer by remember { mutableIntStateOf(1) }

    val pendingTodos = todos.filter { !it.isCompleted }
    val isTimerRunning by PomodoroTimerManager.isRunning.collectAsState()
    val remainingSeconds by PomodoroTimerManager.remainingSeconds.collectAsState()
    val isBreak by PomodoroTimerManager.isBreakMode.collectAsState()

    val mm = remainingSeconds / 60
    val ss = remainingSeconds % 60
    val formattedTime = String.format(Locale.US, "%02d:%02d", mm, ss)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy((-14).dp) // Overlapping stacked slabs
    ) {
        // -------------------------------------------------------------
        // SLAB 0: WATCH / TASKS (C_ORANGE = #FF6A2B)
        // -------------------------------------------------------------
        SlabDrawer(
            index = 0,
            isOpen = openDrawer == 0,
            onToggle = { openDrawer = if (openDrawer == 0) -1 else 0 },
            name = "Tasks",
            info = if (pendingTodos.isEmpty()) "All clear" else "${pendingTodos.size} pending",
            icon = Icons.Default.FormatListBulleted,
            slabColor = SlabOrange,
            inkColor = SlabInk
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "PENDING TASKS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlabInk.copy(alpha = 0.7f),
                            letterSpacing = 1.2.sp,
                            fontFamily = AppleSanFranciscoFontFamily
                        )
                        Text(
                            text = "${pendingTodos.size}",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlabInk,
                            letterSpacing = (-3).sp,
                            fontFamily = AppleSanFranciscoFontFamily,
                            lineHeight = 70.sp
                        )
                    }

                    // Quick button to view all tasks in Deck
                    SlabMiniTile(
                        label = "ALL TASKS",
                        onClick = onNavigateToWidgets,
                        backgroundColor = SlabInk,
                        textColor = SlabOrange
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pendingTodos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, SlabInk, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "No pending tasks. Use '+task' in the command slab.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlabInk,
                            fontFamily = AppleSanFranciscoFontFamily
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        pendingTodos.take(3).forEach { todo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SlabInk.copy(alpha = 0.08f))
                                    .border(1.5.dp, SlabInk, RoundedCornerShape(14.dp))
                                    .clickable { onToggleTodo(todo.id) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, SlabInk, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (todo.isCompleted) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(SlabInk)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = todo.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlabInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = AppleSanFranciscoFontFamily,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // -------------------------------------------------------------
        // SLAB 1: MAKE / TIMER & CLOCK (C_LIME = #DFFF5E)
        // -------------------------------------------------------------
        SlabDrawer(
            index = 1,
            isOpen = openDrawer == 1,
            onToggle = { openDrawer = if (openDrawer == 1) -1 else 1 },
            name = "Timer",
            info = if (isTimerRunning) "$formattedTime running" else "Pomodoro ready",
            icon = Icons.Default.Timer,
            slabColor = SlabLime,
            inkColor = SlabInk
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = if (isTimerRunning) (if (isBreak) "BREAK TIME" else "FOCUS SESSION") else "COUNTDOWN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlabInk.copy(alpha = 0.7f),
                    letterSpacing = 1.2.sp,
                    fontFamily = AppleSanFranciscoFontFamily
                )

                // Giant numeral bleeding off
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SlabInk,
                        letterSpacing = (-3).sp,
                        fontFamily = AppleSanFranciscoFontFamily,
                        lineHeight = 70.sp
                    )

                    // Snappy toggle & reset controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset
                        TactileBox(
                            onClick = { PomodoroTimerManager.reset() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(2.dp, SlabInk, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = SlabInk,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Play / Pause
                        TactileBox(
                            onClick = { PomodoroTimerManager.toggle() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SlabInk),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle",
                                tint = SlabLime,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick preset pills (5m, 15m, 25m, 45m)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 15, 25, 45).forEach { mins ->
                        TactileBox(
                            onClick = {
                                PomodoroTimerManager.setWorkMinutes(mins)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, SlabInk, RoundedCornerShape(12.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlabInk,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // -------------------------------------------------------------
        // SLAB 2: PLAY / NOTES (C_LILAC = #C8B8FF)
        // -------------------------------------------------------------
        SlabDrawer(
            index = 2,
            isOpen = openDrawer == 2,
            onToggle = { openDrawer = if (openDrawer == 2) -1 else 2 },
            name = "Notes",
            info = "${notes.size} notes",
            icon = Icons.Default.EditNote,
            slabColor = SlabLilac,
            inkColor = SlabInk
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "SAVED NOTES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlabInk.copy(alpha = 0.7f),
                            letterSpacing = 1.2.sp,
                            fontFamily = AppleSanFranciscoFontFamily
                        )
                        Text(
                            text = "${notes.size}",
                            fontSize = 68.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlabInk,
                            letterSpacing = (-3).sp,
                            fontFamily = AppleSanFranciscoFontFamily,
                            lineHeight = 70.sp
                        )
                    }

                    SlabMiniTile(
                        label = "+ QUICK MEMO",
                        onClick = onOpenMemo,
                        backgroundColor = SlabInk,
                        textColor = SlabLilac
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (notes.isNotEmpty()) {
                    val latest = notes.first()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlabInk.copy(alpha = 0.08f))
                            .border(1.5.dp, SlabInk, RoundedCornerShape(16.dp))
                            .clickable { onOpenMemo() }
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "LATEST NOTE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlabInk.copy(alpha = 0.6f),
                                letterSpacing = 0.8.sp,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = latest.content,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SlabInk,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, SlabInk, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "No notes yet. Tap '+ QUICK MEMO' or use '*note' command.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlabInk,
                            fontFamily = AppleSanFranciscoFontFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // -------------------------------------------------------------
        // SLAB 3: SYSTEM / DECK (C_SKY = #7FD4FF)
        // -------------------------------------------------------------
        SlabDrawer(
            index = 3,
            isOpen = openDrawer == 3,
            onToggle = { openDrawer = if (openDrawer == 3) -1 else 3 },
            name = "Deck",
            info = "System Ready",
            icon = Icons.Default.Apps,
            slabColor = SlabSky,
            inkColor = SlabInk
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "MINIMAL STACK OS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlabInk.copy(alpha = 0.7f),
                    letterSpacing = 1.2.sp,
                    fontFamily = AppleSanFranciscoFontFamily
                )
                Text(
                    text = "Deck",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SlabInk,
                    letterSpacing = (-2).sp,
                    fontFamily = AppleSanFranciscoFontFamily,
                    lineHeight = 56.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4 Tactile Tiles matching Lumen OS 2 tile_rect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SlabLaunchTile(
                        title = "Apps",
                        subtitle = "Drawer",
                        icon = Icons.Default.Apps,
                        onClick = onNavigateToApps,
                        modifier = Modifier.weight(1f)
                    )
                    SlabLaunchTile(
                        title = "Deck",
                        subtitle = "Widgets",
                        icon = Icons.Default.Dashboard,
                        onClick = onNavigateToWidgets,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SlabLaunchTile(
                        title = "Hub",
                        subtitle = "Feeds",
                        icon = Icons.Default.DynamicFeed,
                        onClick = onNavigateToHub,
                        modifier = Modifier.weight(1f)
                    )
                    SlabLaunchTile(
                        title = "Settings",
                        subtitle = "Prefs",
                        icon = Icons.Default.Settings,
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * An individual accordion Slab drawer with top rounded corners and spring expansion.
 */
@Composable
private fun SlabDrawer(
    index: Int,
    isOpen: Boolean,
    onToggle: () -> Unit,
    name: String,
    info: String,
    icon: ImageVector,
    slabColor: Color,
    inkColor: Color,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "press_scale_$index"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pressScale)
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(slabColor)
            .border(
                width = 2.dp,
                color = inkColor,
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            )
    ) {
        // Tab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onToggle
                )
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = inkColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = inkColor,
                    letterSpacing = (-0.5).sp,
                    fontFamily = AppleSanFranciscoFontFamily
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = info,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor.copy(alpha = 0.75f),
                    fontFamily = AppleSanFranciscoFontFamily
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = inkColor,
                    modifier = Modifier
                        .size(14.dp)
                        .scale(scaleX = 1f, scaleY = if (isOpen) -1f else 1f)
                )
            }
        }

        // Expanded Drawer Body with spring physics
        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically(animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f)) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f)) + fadeOut()
        ) {
            content()
        }
    }
}

/**
 * Tactile tile button for app launch matching Lumen OS 2 `draw_tile`.
 */
@Composable
private fun SlabLaunchTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TactileBox(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SlabInk.copy(alpha = 0.08f))
            .border(2.dp, SlabInk, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SlabInk,
                    modifier = Modifier.size(22.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SlabInk.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlabInk,
                fontFamily = AppleSanFranciscoFontFamily
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlabInk.copy(alpha = 0.65f),
                fontFamily = AppleSanFranciscoFontFamily
            )
        }
    }
}

/**
 * Pill button inside slabs.
 */
@Composable
private fun SlabMiniTile(
    label: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    TactileBox(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontFamily = AppleSanFranciscoFontFamily
        )
    }
}

/**
 * Tactile touch wrapper providing the snappy Lumen OS 2 press physics (0.97f contraction).
 */
@Composable
private fun TactileBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "tactile_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}
