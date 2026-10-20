package com.zaryif.minimalos.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalAskSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var askQuery by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "what's the weather today?",
        "set a reminder for tomorrow morning",
        "nearest coffee shop",
        "calculate 15% tip on 42"
    )

    fun launchQuery(q: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(q)}")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
            onDismiss()
        } catch (_: Exception) {}
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MinimalBlack,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Minimal Ask",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MinimalWhite
                    )
                    Text(
                        text = "your helper to get things done, without distraction",
                        fontSize = 12.sp,
                        color = MinimalGray
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MinimalGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Query Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalRed,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (askQuery.isEmpty()) {
                        Text(
                            text = "ask anything…",
                            style = TextStyle(fontSize = 15.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = askQuery,
                        onValueChange = { askQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 15.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { launchQuery(askQuery) })
                    )
                }

                if (askQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { launchQuery(askQuery) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Search",
                            tint = MinimalRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SUGGESTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MinimalRed,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            quickQuestions.forEach { question ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { launchQuery(question) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = question,
                        fontSize = 14.sp,
                        color = MinimalWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ask",
                        fontSize = 12.sp,
                        color = MinimalDarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
