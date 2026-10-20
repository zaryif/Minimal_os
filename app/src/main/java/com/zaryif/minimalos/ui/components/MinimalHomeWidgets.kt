package com.zaryif.minimalos.ui.components

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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.Todo
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@Composable
fun MinimalHomeWidgets(
    todos: List<Todo>,
    onToggleTodo: (String) -> Unit,
    onOpenMemo: () -> Unit,
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Quick To-Dos preview widget
        val activeTodos = todos.filter { !it.isCompleted }.take(2)
        if (activeTodos.isNotEmpty()) {
            Box(
                modifier = Modifier
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
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FOCUS TO-DOS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalRed,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "view all",
                            fontSize = 11.sp,
                            color = MinimalGray,
                            modifier = Modifier.clickable { onOpenMemo() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    activeTodos.forEach { todo ->
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
                                color = MinimalWhite
                            )
                        }
                    }
                }
            }
        }

        // System Glance Widget (Calendar / Motivation)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Calendar glance
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .clickable { onOpenCalendar() }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CALENDAR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalGray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "view schedule",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite
                        )
                    }
                }
            }

            // Minimal Battery / Status glance
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = MinimalRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DIGITAL DIET",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalGray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "clean & focused",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite
                        )
                    }
                }
            }
        }
    }
}
