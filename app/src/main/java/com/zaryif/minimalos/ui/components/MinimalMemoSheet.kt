package com.zaryif.minimalos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaryif.minimalos.data.repository.StorageRepository
import com.zaryif.minimalos.ui.theme.MinimalBlack
import com.zaryif.minimalos.ui.theme.MinimalCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkCardBg
import com.zaryif.minimalos.ui.theme.MinimalDarkGray
import com.zaryif.minimalos.ui.theme.MinimalGray
import com.zaryif.minimalos.ui.theme.MinimalOnAccent
import com.zaryif.minimalos.ui.theme.MinimalRed
import com.zaryif.minimalos.ui.theme.MinimalWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalMemoSheet(
    storageRepository: StorageRepository,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val notes by storageRepository.notes.collectAsState()
    val todos by storageRepository.todos.collectAsState()
    var selectedTab by remember { mutableStateOf("TO-DO") }
    var searchQuery by remember { mutableStateOf("") }
    var newItemText by remember { mutableStateOf("") }

    val filteredTodos = remember(todos, searchQuery) {
        if (searchQuery.isBlank()) todos
        else todos.filter { it.title.contains(searchQuery.trim(), ignoreCase = true) }
    }

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes
        else notes.filter { it.content.contains(searchQuery.trim(), ignoreCase = true) }
    }

    fun submitNewItem() {
        val trimmed = newItemText.trim()
        if (trimmed.isNotEmpty()) {
            if (selectedTab == "TO-DO") {
                storageRepository.addTodo(trimmed)
            } else {
                storageRepository.addNote(trimmed)
            }
            newItemText = ""
        }
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
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minimal Memo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinimalWhite
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MinimalGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: TO-DO vs NOTES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TO-DO (${todos.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "TO-DO") MinimalRed else MinimalGray,
                    modifier = Modifier.clickable { selectedTab = "TO-DO" }
                )
                Text(
                    text = "NOTES (${notes.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == "NOTES") MinimalRed else MinimalGray,
                    modifier = Modifier.clickable { selectedTab = "NOTES" }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Search Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MinimalDarkCardBg)
                    .border(1.dp, MinimalDarkGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MinimalGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = if (selectedTab == "TO-DO") "Search to-dos…" else "Search notes…",
                            style = TextStyle(fontSize = 13.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MinimalGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Add New Item Input Bar (Context-aware: Adds to To-Do or Notes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MinimalCardBg)
                    .border(1.dp, if (newItemText.isNotBlank()) MinimalRed.copy(alpha = 0.6f) else MinimalDarkGray, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (newItemText.isEmpty()) {
                        Text(
                            text = if (selectedTab == "TO-DO") "add new task (press enter)…" else "write new note (press enter)…",
                            style = TextStyle(fontSize = 13.sp, color = MinimalGray)
                        )
                    }
                    BasicTextField(
                        value = newItemText,
                        onValueChange = { newItemText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        cursorBrush = SolidColor(MinimalRed),
                        textStyle = TextStyle(fontSize = 13.sp, color = MinimalWhite),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submitNewItem() })
                    )
                }
                IconButton(
                    onClick = { submitNewItem() },
                    modifier = Modifier.size(30.dp),
                    enabled = newItemText.isNotBlank()
                ) {
                    Icon(
                        imageVector = if (newItemText.isNotBlank()) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (newItemText.isNotBlank()) MinimalRed else MinimalGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Tab Contents
            if (selectedTab == "TO-DO") {
                if (filteredTodos.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No to-dos matching \"$searchQuery\"" else "No to-dos yet. Type above to add your first task.",
                            fontSize = 13.sp,
                            color = MinimalGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTodos, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MinimalCardBg)
                                    .border(0.5.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                    .clickable { storageRepository.toggleTodo(item.id) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (item.isCompleted) MinimalRed else MinimalDarkCardBg)
                                        .border(1.dp, if (item.isCompleted) MinimalRed else MinimalDarkGray, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (item.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MinimalOnAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    color = if (item.isCompleted) MinimalGray else MinimalWhite,
                                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { storageRepository.deleteTodo(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MinimalDarkGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                if (filteredNotes.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No notes matching \"$searchQuery\"" else "No notes yet. Type above to write your first note.",
                            fontSize = 13.sp,
                            color = MinimalGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredNotes, key = { it.id }) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MinimalCardBg)
                                    .border(0.5.dp, MinimalDarkGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.content,
                                        fontSize = 14.sp,
                                        color = MinimalWhite,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { storageRepository.deleteNote(item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MinimalDarkGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
