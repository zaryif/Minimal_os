package com.zaryif.minimalos.ui.hub

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.NotificationCategory
import com.zaryif.minimalos.domain.model.NotificationItem
import com.zaryif.minimalos.service.MinimalNotificationService
import com.zaryif.minimalos.ui.components.ButtonStyle
import com.zaryif.minimalos.ui.components.MinimalButton
import com.zaryif.minimalos.ui.components.MinimalCard
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import java.util.concurrent.TimeUnit

@Composable
fun HubScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val notifications by MinimalNotificationService.notifications.collectAsState()
    var selectedCategory by remember { mutableStateOf(NotificationCategory.ALL) }
    val isPermissionGranted = remember { MinimalNotificationService.isPermissionGranted(context) }

    val categories = listOf(
        NotificationCategory.ALL,
        NotificationCategory.MESSAGE,
        NotificationCategory.CALLS,
        NotificationCategory.EMAIL
    )

    val filteredList = remember(notifications, selectedCategory) {
        if (selectedCategory == NotificationCategory.ALL) {
            notifications
        } else {
            notifications.filter { it.category == selectedCategory }
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
                    // Swipe left -> Home
                    if (x < -30 && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                        onBackToHome()
                    }
                }
            }
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHome, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to home",
                        tint = MinimalWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "hub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "messages, calls, and email alerts in one unified list, so\nthere's one place to look rather than nine.",
                fontSize = 13.sp,
                color = MinimalGray,
                lineHeight = 18.sp
            )

            // Permission status banner if listener service is disabled
            if (!isPermissionGranted) {
                Spacer(modifier = Modifier.height(14.dp))
                MinimalCard(
                    title = "notification access required",
                    subtitle = "tap to grant system permission so Hub receives alerts live",
                    icon = Icons.Default.Warning,
                    iconColor = MinimalRed,
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    val count = if (cat == NotificationCategory.ALL) {
                        notifications.size
                    } else {
                        notifications.count { it.category == cat }
                    }

                    Text(
                        text = if (count > 0) "${cat.displayName} ($count)" else cat.displayName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = if (isSelected) MinimalWhite else MinimalGray,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "no alerts in ${selectedCategory.displayName.lowercase()}",
                        fontSize = 14.sp,
                        color = MinimalGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.key.ifEmpty { it.id.toString() } }) { item ->
                        val icon = when (item.category) {
                            NotificationCategory.MESSAGE -> Icons.Default.ChatBubbleOutline
                            NotificationCategory.CALLS -> Icons.Default.Phone
                            NotificationCategory.EMAIL -> Icons.Default.Email
                            else -> Icons.Default.NotificationsNone
                        }

                        MinimalCard(
                            title = item.title,
                            subtitle = item.content,
                            icon = icon,
                            iconColor = MinimalRed,
                            trailingText = formatTimestamp(item.timestamp),
                            onClick = {
                                // Try dispatching the pending intent directly or launch app
                                if (item.pendingIntent != null) {
                                    try {
                                        item.pendingIntent.send()
                                    } catch (_: Exception) {
                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(item.packageName)
                                        launchIntent?.let { context.startActivity(it) }
                                    }
                                } else {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(item.packageName)
                                    launchIntent?.let { context.startActivity(it) }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clear All / Dismiss button
            if (filteredList.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "clear alerts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalGray,
                        modifier = Modifier.clickable {
                            filteredList.forEach { MinimalNotificationService.dismiss(it.key) }
                        }
                    )
                    Text(
                        text = "← SWIPE LEFT FOR HOME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MinimalRed
                    )
                }
            } else {
                Text(
                    text = "← SWIPE LEFT FOR HOME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MinimalRed,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${hours / 24}d"
    }
}
