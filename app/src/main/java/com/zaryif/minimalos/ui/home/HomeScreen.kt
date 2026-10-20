package com.zaryif.minimalos.ui.home

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.data.repository.ContactRepository
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.domain.model.Contact
import com.zaryif.minimalos.domain.model.RestrictionMode
import com.zaryif.minimalos.domain.usecase.CommandResult
import com.zaryif.minimalos.domain.usecase.ExecuteCommandUseCase
import com.zaryif.minimalos.ui.components.CommandBar
import com.zaryif.minimalos.ui.components.MinimalHomeWidgets
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    restrictionMode: RestrictionMode,
    installedApps: List<AppInfo>,
    storageRepository: StorageRepository,
    contactRepository: ContactRepository,
    executeCommandUseCase: ExecuteCommandUseCase,
    onNavigateToApps: () -> Unit,
    onNavigateToHub: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenMemo: () -> Unit,
    onOpenClock: () -> Unit,
    onOpenAsk: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val todos by storageRepository.todos.collectAsState()

    var query by remember { mutableStateOf("") }
    var matchingApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var matchingContacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding() // Ensures command bar floats cleanly above on-screen keyboard
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val (x, y) = dragAmount
                    // Swipe up -> Apps
                    if (y < -30 && kotlin.math.abs(y) > kotlin.math.abs(x)) {
                        onNavigateToApps()
                    }
                    // Swipe right -> Hub
                    if (x > 30 && kotlin.math.abs(x) > kotlin.math.abs(y)) {
                        onNavigateToHub()
                    }
                }
            }
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Restriction Badge + Mini Apps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (restrictionMode != RestrictionMode.UNRESTRICTED) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MinimalDarkGray)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = restrictionMode.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Mini Apps Icon Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenAsk, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Ask",
                            tint = MinimalGray,
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
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Memo",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
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

            // Center Area: Minimal Clock + Live Home Widgets
            Column(
                modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(20.dp))

                // Minimal Home Widgets (To-Dos & Calendar/Status)
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
            }

            // Bottom Area: Hints + CommandBar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "➔ swipe right for hub",
                        fontSize = 11.sp,
                        color = MinimalDarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "↑ swipe up for apps",
                        fontSize = 11.sp,
                        color = MinimalDarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Command Bar with live contacts and apps autocomplete
                CommandBar(
                    query = query,
                    onQueryChange = { query = it },
                    onExecute = { input ->
                        val result = executeCommandUseCase.execute(input)
                        when (result) {
                            is CommandResult.Success -> {
                                query = ""
                                scope.launch { snackbarHostState.showSnackbar(result.message) }
                            }
                            is CommandResult.LaunchedApp -> {
                                query = ""
                            }
                            is CommandResult.Error -> {
                                scope.launch { snackbarHostState.showSnackbar(result.error) }
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
