package com.zaryif.minimalos.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.zaryif.minimalos.domain.model.NotificationCategory
import com.zaryif.minimalos.domain.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MinimalNotificationService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        refreshActiveNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let { addNotification(it) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.let { removeNotification(it.id) }
    }

    private fun addNotification(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val pkg = sbn.packageName

        if (title.isBlank() && text.isBlank()) return
        if (pkg == packageName) return // Ignore self

        val category = categorize(pkg, sbn.notification.category)

        val item = NotificationItem(
            id = sbn.id,
            appName = getAppName(pkg),
            title = title,
            content = text,
            timestamp = sbn.postTime,
            packageName = pkg,
            category = category
        )

        val current = _notifications.value.filterNot { it.id == sbn.id }
        _notifications.value = listOf(item) + current
    }

    private fun removeNotification(id: Int) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }

    private fun refreshActiveNotifications() {
        try {
            val active = activeNotifications ?: return
            val list = mutableListOf<NotificationItem>()
            for (sbn in active) {
                val extras = sbn.notification.extras
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                val pkg = sbn.packageName
                if (title.isBlank() && text.isBlank()) continue
                if (pkg == packageName) continue

                val category = categorize(pkg, sbn.notification.category)
                list.add(
                    NotificationItem(
                        id = sbn.id,
                        appName = getAppName(pkg),
                        title = title,
                        content = text,
                        timestamp = sbn.postTime,
                        packageName = pkg,
                        category = category
                    )
                )
            }
            _notifications.value = list.sortedByDescending { it.timestamp }
        } catch (_: Exception) {}
    }

    private fun categorize(pkg: String, androidCategory: String?): NotificationCategory {
        val lowerPkg = pkg.lowercase()
        return when {
            androidCategory == Notification.CATEGORY_MESSAGE ||
            lowerPkg.contains("sms") || lowerPkg.contains("mms") ||
            lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") ||
            lowerPkg.contains("signal") || lowerPkg.contains("messenger") -> NotificationCategory.MESSAGE

            androidCategory == Notification.CATEGORY_CALL ||
            androidCategory == Notification.CATEGORY_MISSED_CALL ||
            lowerPkg.contains("dialer") || lowerPkg.contains("phone") ||
            lowerPkg.contains("telecom") -> NotificationCategory.CALLS

            androidCategory == Notification.CATEGORY_EMAIL ||
            lowerPkg.contains("gmail") || lowerPkg.contains("mail") ||
            lowerPkg.contains("outlook") -> NotificationCategory.EMAIL

            else -> NotificationCategory.APPS
        }
    }

    private fun getAppName(packageName: String): String {
        return try {
            val pm = packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (_: Exception) {
            packageName.substringAfterLast('.')
        }
    }

    companion object {
        private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

        fun setMockDataIfEmpty() {
            if (_notifications.value.isEmpty()) {
                val now = System.currentTimeMillis()
                _notifications.value = listOf(
                    NotificationItem(
                        id = 1,
                        appName = "Messages",
                        title = "jason m.",
                        content = "on my way",
                        timestamp = now - 60_000,
                        packageName = "com.google.android.apps.messaging",
                        category = NotificationCategory.MESSAGE
                    ),
                    NotificationItem(
                        id = 2,
                        appName = "Phone",
                        title = "mum",
                        content = "missed call",
                        timestamp = now - 5 * 60_000,
                        packageName = "com.google.android.dialer",
                        category = NotificationCategory.CALLS
                    ),
                    NotificationItem(
                        id = 3,
                        appName = "Gmail",
                        title = "amara w.",
                        content = "lunch thursday?",
                        timestamp = now - 60 * 60_000,
                        packageName = "com.google.android.gm",
                        category = NotificationCategory.EMAIL
                    )
                )
            }
        }
    }
}
