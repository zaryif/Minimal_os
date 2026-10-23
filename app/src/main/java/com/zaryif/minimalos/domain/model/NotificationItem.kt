package com.zaryif.minimalos.domain.model

import android.app.PendingIntent

data class NotificationItem(
    val id: Int,
    val key: String = "",
    val appName: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val packageName: String,
    val category: NotificationCategory,
    val isFlagged: Boolean = false,
    val pendingIntent: PendingIntent? = null
)

enum class NotificationCategory(val displayName: String) {
    ALL("ALL"),
    MESSAGE("MESSAGE"),
    CALLS("CALLS"),
    EMAIL("EMAIL"),
    OTHER("OTHER")
}
