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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zaryif.minimalos.ui.theme.AppleSanFranciscoFontFamily
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalOnAccent
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import java.util.Calendar
import java.util.Locale

@Composable
fun MinimalAlarmTimePickerDialog(
    initialHour: Int = 7,
    initialMinute: Int = 0,
    initialAmPm: String = "AM",
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, amPm: String, dayLabel: String) -> Unit
) {
    val now = remember { Calendar.getInstance() }
    val defaultH = remember {
        val rawH = now.get(Calendar.HOUR)
        if (rawH == 0) 12 else rawH
    }
    val defaultAmPm = remember {
        if (now.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
    }

    var selectedHour by remember { mutableIntStateOf(if (initialHour in 1..12) initialHour else defaultH) }
    var selectedMinute by remember { mutableIntStateOf(if (initialMinute in 0..59) initialMinute else 0) }
    var selectedAmPm by remember { mutableStateOf(if (initialAmPm.equals("PM", ignoreCase = true)) "PM" else defaultAmPm) }
    var selectedDay by remember { mutableStateOf("Tomorrow") }

    val dayOptions = listOf("Today", "Tomorrow", "Daily", "Weekdays")
    val commonMinutes = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MinimalCardBg)
                .border(1.5.dp, MinimalDarkGray, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SET ALARM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero Time Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MinimalDarkCardBg)
                        .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute),
                            fontFamily = AppleSanFranciscoFontFamily,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalWhite,
                            letterSpacing = (-1).sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedAmPm,
                            fontFamily = AppleSanFranciscoFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalRed,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Hour Selector (1 - 12)
                Text(
                    text = "HOUR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalGray,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items((1..12).toList()) { hour ->
                        val isSelected = hour == selectedHour
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                                .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                                .clickable { selectedHour = hour },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$hour",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MinimalOnAccent else MinimalWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Minute Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MINUTE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalGray,
                        letterSpacing = 1.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "-1m",
                            fontSize = 10.sp,
                            color = MinimalRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { selectedMinute = (selectedMinute - 1 + 60) % 60 }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Text(
                            text = "+1m",
                            fontSize = 10.sp,
                            color = MinimalRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { selectedMinute = (selectedMinute + 1) % 60 }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(commonMinutes) { minute ->
                        val isSelected = minute == selectedMinute
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                                .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                                .clickable { selectedMinute = minute },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format(Locale.US, "%02d", minute),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MinimalOnAccent else MinimalWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. AM / PM & Day Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AM / PM Pills
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MinimalDarkCardBg)
                            .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("AM", "PM").forEach { ampm ->
                            val isSelected = ampm == selectedAmPm
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                                    .clickable { selectedAmPm = ampm }
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ampm,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MinimalOnAccent else MinimalWhite
                                )
                            }
                        }
                    }

                    // Day Selectors
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(dayOptions) { day ->
                            val isSelected = day == selectedDay
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                                    .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                                    .clickable { selectedDay = day }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MinimalOnAccent else MinimalWhite
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions: Cancel & Set Alarm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "CANCEL", color = MinimalGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(selectedHour, selectedMinute, selectedAmPm, selectedDay)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MinimalRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SET ALARM",
                            color = MinimalOnAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
