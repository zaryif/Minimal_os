package com.zaryif.minimalos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.domain.model.CommandType
import com.zaryif.minimalos.domain.model.Contact
import com.zaryif.minimalos.domain.parser.CommandParser
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@Composable
fun CommandBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onExecute: (String) -> Unit,
    matchingApps: List<AppInfo>,
    onAppSelected: (AppInfo) -> Unit,
    matchingContacts: List<Contact>,
    onContactSelected: (Contact) -> Unit,
    onHelpRequested: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val command = CommandParser.parse(query)
    val hasPrefix = query.isNotBlank() && CommandType.fromSymbol(query.trim().first()) != null
    val isCallingOrMessaging = command.type == CommandType.CALL ||
            command.type == CommandType.MESSAGE ||
            command.type == CommandType.WHATSAPP

    fun submitCommand(input: String) {
        keyboardController?.hide()
        onExecute(input)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Contacts auto-complete suggestions if calling/messaging/whatsapp (only when contact is not yet locked with :)
        AnimatedVisibility(
            visible = isCallingOrMessaging && !query.contains(":") && matchingContacts.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                matchingContacts.take(5).forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (command.type == CommandType.WHATSAPP || query.lowercase().startsWith("wa")) {
                                    onQueryChange("wa ${contact.name}: ")
                                } else if (command.type == CommandType.CALL || query.startsWith("#")) {
                                    submitCommand("#${contact.phoneNumber}")
                                } else {
                                    onQueryChange("@ ${contact.name}: ")
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MinimalWhite
                            )
                            Text(
                                text = contact.phoneNumber,
                                fontSize = 11.sp,
                                color = MinimalGray
                            )
                        }

                        // WhatsApp shortcut chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MinimalDarkCardBg)
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(6.dp))
                                .clickable { onQueryChange("wa ${contact.name}: ") }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp",
                                tint = MinimalRed,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "WA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MinimalWhite)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // SMS shortcut chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MinimalDarkCardBg)
                                .border(1.dp, MinimalDarkGray, RoundedCornerShape(6.dp))
                                .clickable { onQueryChange("@ ${contact.name}: ") }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "SMS",
                                tint = MinimalRed,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "SMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MinimalWhite)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Call direct button
                        IconButton(
                            onClick = { submitCommand("#${contact.phoneNumber}") },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = MinimalRed,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. App auto-complete suggestions if searching apps (constrained height with scroll)
        AnimatedVisibility(
            visible = !hasPrefix && query.isNotBlank() && !isCallingOrMessaging && matchingApps.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .heightIn(max = 180.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                matchingApps.take(4).forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                keyboardController?.hide()
                                onAppSelected(app)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open app",
                            tint = MinimalGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 3. Command Bar Input Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MinimalCardBg)
                .border(1.dp, MinimalDarkGray, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Command badge if active prefix or keyword detected
                if (hasPrefix || (query.contains(":") && (command.type == CommandType.MESSAGE || command.type == CommandType.WHATSAPP))) {
                    val badgeText = if (query.contains(":")) {
                        val recipient = query.substringBefore(":").replace("whatsapp", "").replace("wa", "").replace("msg", "").replace("@", "").replace("=", "").trim()
                        if (command.type == CommandType.WHATSAPP || query.lowercase().startsWith("wa")) "WA: $recipient" else "SMS: $recipient"
                    } else {
                        "${command.type.symbol} ${command.type.displayName}"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MinimalRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Text field
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "type to do things or search…",
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = MinimalGray,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = MinimalWhite,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { submitCommand(query) }
                        )
                    )
                }

                // Help or Clear / Execute buttons
                if (query.isEmpty() && onHelpRequested != null) {
                    IconButton(
                        onClick = onHelpRequested,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Commands Help",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MinimalGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { submitCommand(query) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Execute",
                            tint = MinimalRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
