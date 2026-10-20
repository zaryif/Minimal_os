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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zaryif.minimalos.data.preferences.AiProvider
import com.zaryif.minimalos.data.preferences.UserPreferences
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalAskSheet(
    userPreferences: UserPreferences,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedProvider by userPreferences.aiProvider.collectAsState(initial = AiProvider.GEMINI)

    var askQuery by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "what's the weather today?",
        "summarize my schedule",
        "calculate 15% tip on 42",
        "nearest coffee shop"
    )

    fun launchQuery(q: String) {
        if (q.isBlank()) return
        val url = when (selectedProvider) {
            AiProvider.GOOGLE -> "https://www.google.com/search?q=${Uri.encode(q)}"
            AiProvider.GEMINI -> "https://gemini.google.com/app?q=${Uri.encode(q)}"
            AiProvider.CHATGPT -> "https://chatgpt.com/?q=${Uri.encode(q)}"
            AiProvider.CLAUDE -> "https://claude.ai/new?q=${Uri.encode(q)}"
        }

        // Try app first if provider has package
        if (selectedProvider.packageName != null) {
            val appIntent = context.packageManager.getLaunchIntentForPackage(selectedProvider.packageName!!)
            if (appIntent != null) {
                appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                appIntent.putExtra(Intent.EXTRA_TEXT, q)
                try {
                    context.startActivity(appIntent)
                    onDismiss()
                    return
                } catch (_: Exception) {}
            }
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(browserIntent)
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
                .fillMaxHeight(0.75f)
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
                        text = "powered by ${selectedProvider.displayName}",
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

            Spacer(modifier = Modifier.height(16.dp))

            // AI Model / Search Engine Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiProvider.entries.forEach { provider ->
                    val isSelected = provider == selectedProvider
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MinimalRed else MinimalDarkCardBg)
                            .border(1.dp, if (isSelected) MinimalRed else MinimalDarkGray, RoundedCornerShape(8.dp))
                            .clickable {
                                scope.launch { userPreferences.setAiProvider(provider) }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = provider.displayName.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MinimalWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                            text = "ask ${selectedProvider.displayName}…",
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
                            contentDescription = "Ask",
                            tint = MinimalRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

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
