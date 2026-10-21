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
import androidx.compose.runtime.getValue
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.Search
import com.zaryif.minimalos.domain.model.AppInfo
import com.zaryif.minimalos.domain.model.CommandType
import com.zaryif.minimalos.domain.model.Contact
import com.zaryif.minimalos.domain.parser.CommandParser
import com.zaryif.minimalos.ui.theme.AppleSanFranciscoFontFamily
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import com.zaryif.minimalos.ui.theme.SlabInk
import com.zaryif.minimalos.ui.theme.SlabLime
import com.zaryif.minimalos.ui.theme.SlabOrange
import com.zaryif.minimalos.ui.theme.SlabPaper
import com.zaryif.minimalos.ui.theme.currentActiveTheme

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

    val isMinimalStack = currentActiveTheme.id == "MINIMAL_STACK"

    val containerBg by animateColorAsState(
        targetValue = if (isMinimalStack) {
            if (query.isNotEmpty()) SlabLime else SlabPaper
        } else {
            MinimalCardBg
        },
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "slab_bg"
    )
    val containerBorderColor by animateColorAsState(
        targetValue = if (isMinimalStack) {
            if (query.isNotEmpty()) SlabInk else SlabPaper
        } else {
            MinimalDarkGray
        },
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "slab_border"
    )

    val textColor = if (isMinimalStack) SlabInk else MinimalWhite
    val placeholderColor = if (isMinimalStack) SlabInk.copy(alpha = 0.6f) else MinimalGray
    val actionTint = if (isMinimalStack) SlabInk else MinimalRed
    val iconTint = if (isMinimalStack) SlabInk else MinimalGray
    val cursorColor = if (isMinimalStack) SlabOrange else MinimalRed
    val containerShape = RoundedCornerShape(if (isMinimalStack) 22.dp else 16.dp)
    val containerBorderWidth = if (isMinimalStack) 2.dp else 1.dp

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Contacts auto-complete suggestions if calling/messaging/whatsapp (only when contact is not yet locked with :)
        AnimatedVisibility(
            visible = isCallingOrMessaging && !query.contains(":") && matchingContacts.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val popupShape = RoundedCornerShape(if (isMinimalStack) 18.dp else 12.dp)
            val popupBg = if (isMinimalStack) SlabPaper else MinimalCardBg
            val popupBorder = if (isMinimalStack) SlabInk else MinimalDarkGray
            val popupBorderWidth = if (isMinimalStack) 2.dp else 1.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(popupShape)
                    .background(popupBg)
                    .border(popupBorderWidth, popupBorder, popupShape)
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
                            tint = if (isMinimalStack) SlabInk else MinimalRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isMinimalStack) SlabInk else MinimalWhite,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                            Text(
                                text = contact.phoneNumber,
                                fontSize = 11.sp,
                                color = if (isMinimalStack) SlabInk.copy(alpha = 0.65f) else MinimalGray,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                        }

                        // WhatsApp shortcut chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isMinimalStack) SlabInk else MinimalDarkCardBg)
                                .border(1.dp, if (isMinimalStack) SlabInk else MinimalDarkGray, RoundedCornerShape(6.dp))
                                .clickable { onQueryChange("wa ${contact.name}: ") }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp",
                                tint = if (isMinimalStack) SlabLime else MinimalRed,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "WA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMinimalStack) SlabPaper else MinimalWhite,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // SMS shortcut chip
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isMinimalStack) SlabInk else MinimalDarkCardBg)
                                .border(1.dp, if (isMinimalStack) SlabInk else MinimalDarkGray, RoundedCornerShape(6.dp))
                                .clickable { onQueryChange("@ ${contact.name}: ") }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "SMS",
                                tint = if (isMinimalStack) SlabLime else MinimalRed,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "SMS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMinimalStack) SlabPaper else MinimalWhite,
                                fontFamily = AppleSanFranciscoFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Call direct chip
                        IconButton(
                            onClick = { submitCommand("#${contact.phoneNumber}") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = if (isMinimalStack) SlabInk else MinimalRed,
                                modifier = Modifier.size(16.dp)
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
            val popupShape = RoundedCornerShape(if (isMinimalStack) 18.dp else 12.dp)
            val popupBg = if (isMinimalStack) SlabPaper else MinimalCardBg
            val popupBorder = if (isMinimalStack) SlabInk else MinimalDarkGray
            val popupBorderWidth = if (isMinimalStack) 2.dp else 1.dp

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(popupShape)
                    .background(popupBg)
                    .border(popupBorderWidth, popupBorder, popupShape)
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
                            fontWeight = FontWeight.SemiBold,
                            color = if (isMinimalStack) SlabInk else MinimalWhite,
                            modifier = Modifier.weight(1f),
                            fontFamily = AppleSanFranciscoFontFamily
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open app",
                            tint = if (isMinimalStack) SlabInk else MinimalGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 3. Command Bar Input Box / Slab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(containerShape)
                .background(containerBg)
                .border(containerBorderWidth, containerBorderColor, containerShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // If in Minimal Stack theme and no command badge, show the iconic Lumen OS 2 search icon
                if (isMinimalStack && !hasPrefix && !query.contains(":")) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SlabInk,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

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
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMinimalStack) SlabInk else MinimalRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isMinimalStack) SlabLime else MinimalWhite,
                            fontFamily = AppleSanFranciscoFontFamily
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
                            text = if (isMinimalStack) "search the web or type command…" else "type to do things or search…",
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = placeholderColor,
                                fontWeight = if (isMinimalStack) FontWeight.Medium else FontWeight.Normal,
                                fontFamily = AppleSanFranciscoFontFamily
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
                        cursorBrush = SolidColor(cursorColor),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            color = textColor,
                            fontWeight = if (isMinimalStack) FontWeight.SemiBold else FontWeight.Normal,
                            lineHeight = 20.sp,
                            fontFamily = AppleSanFranciscoFontFamily
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
                            tint = iconTint,
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
                            tint = iconTint,
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
                            tint = actionTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
