package com.zaryif.minimalos.domain.model

data class NotificationItem(
    val id: Int,
    val appName: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val packageName: String,
    val category: NotificationCategory,
    val isFlagged: Boolean = false
)

enum class NotificationCategory(val displayName: String) {
    ALL("ALL"),
    MESSAGE("MESSAGE"),
    CALLS("CALLS"),
    EMAIL("EMAIL"),
    APPS("APPS"),
    FLAGGED("FLAGGED")
}
