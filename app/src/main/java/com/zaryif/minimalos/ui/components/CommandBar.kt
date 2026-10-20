package com.zaryif.minimalos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
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
    modifier: Modifier = Modifier
) {
    val command = CommandParser.parse(query)
    val hasPrefix = command.type != CommandType.SEARCH && query.isNotEmpty()
    val isCallingOrMessaging = command.type == CommandType.CALL || command.type == CommandType.MESSAGE

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Contacts auto-complete suggestions if calling or messaging
        AnimatedVisibility(
            visible = isCallingOrMessaging && matchingContacts.isNotEmpty(),
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
            ) {
                matchingContacts.take(4).forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onContactSelected(contact) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MinimalWhite
                            )
                            Text(
                                text = contact.phoneNumber,
                                fontSize = 12.sp,
                                color = MinimalGray
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = MinimalRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. App auto-complete suggestions if searching apps
        AnimatedVisibility(
            visible = !hasPrefix && query.isNotBlank() && matchingApps.isNotEmpty(),
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
            ) {
                matchingApps.take(3).forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAppSelected(app) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.label,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MinimalWhite
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "launch",
                            fontSize = 12.sp,
                            color = MinimalRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Main input container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MinimalCardBg)
                .border(
                    1.5.dp,
                    if (hasPrefix) MinimalRed else MinimalDarkGray,
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Command badge if active prefix or keyword detected
                if (hasPrefix) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MinimalRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${command.type.symbol} ${command.type.displayName}",
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
                                fontSize = 14.sp,
                                color = MinimalGray,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
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
                            onGo = { onExecute(query) }
                        )
                    )
                }
                        keyboardActions = KeyboardActions(
                            onGo = { onExecute(query) }
                        )
                    )
                }

                // Clear or Execute button
                if (query.isNotEmpty()) {
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
                        onClick = { onExecute(query) },
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
