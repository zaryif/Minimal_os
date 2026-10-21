package com.zaryif.minimalos.ui.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.data.repository.ContactRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.domain.model.Contact
import com.zaryif.minimalos.domain.usecase.CommandResult
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.ui.components.CommandBar
import com.zaryif.minimalos.ui.components.MinimalHomeWidgets
import com.zaryif.minimalos.ui.components.MinimalNotificationBanner
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.delay
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    installedApps: List<AppInfo>,
    storageRepository: StorageRepository,
    contactRepository: ContactRepository,
    userPreferences: UserPreferences,
    executeCommandUseCase: ExecuteCommandUseCase,
    onNavigateToApps: () -> Unit,
    onNavigateToHub: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenMemo: () -> Unit,
    onOpenClock: () -> Unit
) {
    val context = LocalContext.current
    val todos by storageRepository.todos.collectAsState()
    val wallpaperUriString by userPreferences.customWallpaperUri.collectAsState(initial = null)
    val favoriteAppPackages by userPreferences.favoriteApps.collectAsState(initial = emptySet())

    var notificationMessage by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var matchingApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var matchingContacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

    val isImeVisible = WindowInsets.isImeVisible
    val isTyping = isImeVisible || query.isNotEmpty()

    val favoriteApps = remember(favoriteAppPackages, installedApps) {
        installedApps.filter { it.packageName in favoriteAppPackages }
    }

    // Live clock
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now).lowercase()
            delay(1000)
        }
    }

    // Dynamic autocomplete for apps and contacts as query changes
    LaunchedEffect(query) {
        val lower = query.lowercase().trim()

        // 1. Contacts search for calling/messaging
        matchingContacts = if (lower.startsWith("#") || lower.startsWith("call ") || lower.startsWith("dial ") ||
            lower.startsWith("@") || lower.startsWith("msg ") || lower.startsWith("sms ")
        ) {
            val contactQuery = when {
                lower.startsWith("#") || lower.startsWith("@") -> lower.drop(1).trim()
                lower.startsWith("call ") -> lower.removePrefix("call ").trim()
                lower.startsWith("dial ") -> lower.removePrefix("dial ").trim()
                lower.startsWith("msg ") -> lower.removePrefix("msg ").trim()
                lower.startsWith("sms ") -> lower.removePrefix("sms ").trim()
                else -> ""
            }
            if (contactQuery.isNotEmpty()) {
                contactRepository.searchContacts(contactQuery)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }

        // 2. Apps search
        matchingApps = if (query.isNotBlank() && !query.startsWith("@") &&
            !query.startsWith("#") && !query.startsWith("-") &&
            !query.startsWith("!") && !query.startsWith("*") &&
            !query.startsWith("+") && !query.startsWith(":") &&
            !query.startsWith("?") && !query.startsWith("^") &&
            !query.startsWith("&") && !lower.startsWith("call ") &&
            !lower.startsWith("dial ") && !lower.startsWith("msg ") &&
            !lower.startsWith("note ") && !lower.startsWith("todo ") &&
            !lower.startsWith("event ") && !lower.startsWith("alarm ") &&
            !lower.startsWith("timer ") && !lower.startsWith("drive ") &&
            !lower.startsWith("email ")
        ) {
            installedApps.filter { it.label.lowercase().contains(lower) }
        } else {
            emptyList()
        }
    }

    // Auto-dismiss banner after 3 seconds
    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(3000)
            notificationMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
    ) {
        // Optional Custom Wallpaper background from photos
        if (!wallpaperUriString.isNullOrBlank()) {
            val bitmap = remember(wallpaperUriString) {
                try {
                    val uri = Uri.parse(wallpaperUriString)
                    val input: InputStream? = context.contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(input)
                } catch (_: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Custom Wallpaper",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark scrim over wallpaper to guarantee high contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.78f))
                )
            }
        }

        // Main Home Screen Layout anchored perfectly above keyboard
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val (x, y) = dragAmount
                        if (!isTyping) {
                            // Swipe up -> Apps
                            if (y < -30 && kotlin.math.abs(y) > kotlin.math.abs(x)) {
                                onNavigateToApps()
                            }
                            // Swipe right -> Hub
                            if (x > 30 && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                                onNavigateToHub()
                            }
                            // Swipe left -> Widgets Deck
                            if (x < -30 && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                                onNavigateToWidgets()
                            }
                        }
                    }
                }
                .padding(horizontal = 22.dp, vertical = 8.dp)
        ) {
            // 1. Top Bar: Title & Mini Apps Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "minimal",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite,
                    letterSpacing = 1.sp
                )

                // Mini Apps Shortcuts (Assistant, Clock, Memo, Settings)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { launchNativeAssistant(context) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Assistant",
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onOpenClock, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Clock",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onOpenMemo, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Memo",
                            tint = MinimalGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Center Area: WEIGHT(1f) ensures it shrinks when keyboard opens so CommandBar is NEVER hidden
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!isTyping) {
                    // Full home screen widgets & large clock
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentTime,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-2).sp,
                            color = MinimalWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MinimalGray
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Minimal Home Widgets (Focus To-Dos & Calendar Glance)
                        MinimalHomeWidgets(
                            todos = todos,
                            onToggleTodo = { id -> storageRepository.toggleTodo(id) },
                            onOpenMemo = onOpenMemo,
                            onOpenCalendar = {
                                val calIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                try {
                                    context.startActivity(calIntent)
                                } catch (_: Exception) {}
                            }
                        )

                        // User-configured Favorite App Shortcuts (if configured in Settings)
                        if (favoriteApps.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                favoriteApps.take(4).forEach { app ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MinimalDarkCardBg)
                                            .border(1.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                                intent?.let { context.startActivity(it) }
                                            }
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
                    }
                } else {
                    // Compact header when typing to leave maximum space for suggestions and input
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentTime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalGray
                        )
                        Text(
                            text = "minimal terminal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalDarkGray
                        )
                    }
                }
            }

            // 3. Bottom Area: Gesture hints (only when idle) + CommandBar (ALWAYS VISIBLE ABOVE KEYBOARD)
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isTyping) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "➔ hub",
                            fontSize = 11.sp,
                            color = MinimalDarkGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "↑ apps",
                            fontSize = 11.sp,
                            color = MinimalDarkGray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "widgets ➔",
                            fontSize = 11.sp,
                            color = MinimalDarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Command Bar anchored firmly at bottom of visible window
                CommandBar(
                    query = query,
                    onQueryChange = { query = it },
                    onExecute = { input ->
                        val result = executeCommandUseCase.execute(input)
                        when (result) {
                            is CommandResult.Success -> {
                                query = ""
                                notificationMessage = result.message
                            }
                            is CommandResult.LaunchedApp -> {
                                query = ""
                            }
                            is CommandResult.Error -> {
                                notificationMessage = result.error
                            }
                        }
                    },
                    matchingApps = matchingApps,
                    onAppSelected = { app ->
                        query = ""
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                        launchIntent?.let { context.startActivity(it) }
                    },
                    matchingContacts = matchingContacts,
                    onContactSelected = { contact ->
                        query = ""
                        executeCommandUseCase.execute("#${contact.phoneNumber}")
                    }
                )
            }
        }

        // Minimal OS Pop Banner (Top aligned)
        MinimalNotificationBanner(
            message = notificationMessage,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

private fun launchNativeAssistant(context: Context) {
    val voiceIntent = Intent(Intent.ACTION_VOICE_COMMAND).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(voiceIntent)
    } catch (_: Exception) {
        val assistIntent = Intent(Intent.ACTION_ASSIST).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(assistIntent)
        } catch (_: Exception) {
            val geminiIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.bard")
            if (geminiIntent != null) {
                geminiIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(geminiIntent)
            } else {
                val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, "")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(searchIntent)
                } catch (_: Exception) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gemini.google.com/app")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(browserIntent)
                }
            }
        }
    }
}
