package com.zaryif.minimalos.data.repository

import android.content.Context
import com.zaryif.minimalos.domain.model.Note
import com.zaryif.minimalos.domain.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class StorageRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("minimal_data", Context.MODE_PRIVATE)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    init {
        loadNotes()
        loadTodos()
    }

    private fun loadNotes() {
        val jsonStr = prefs.getString("notes_json", "[]") ?: "[]"
        val list = mutableListOf<Note>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Note(
                        id = obj.getString("id"),
                        content = obj.getString("content"),
                        createdAt = obj.getLong("createdAt")
                    )
                )
            }
        } catch (_: Exception) {}
        _notes.value = list
    }

    private fun saveNotes(list: List<Note>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("content", item.content)
                put("createdAt", item.createdAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("notes_json", arr.toString()).apply()
        _notes.value = list
    }

    fun addNote(content: String): Note {
        val note = Note(content = content)
        val updated = listOf(note) + _notes.value
        saveNotes(updated)
        return note
    }

    fun deleteNote(id: String) {
        val updated = _notes.value.filterNot { it.id == id }
        saveNotes(updated)
    }

    private fun loadTodos() {
        val jsonStr = prefs.getString("todos_json", "[]") ?: "[]"
        val list = mutableListOf<Todo>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Todo(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isCompleted = obj.getBoolean("isCompleted"),
                        createdAt = obj.getLong("createdAt")
                    )
                )
            }
        } catch (_: Exception) {}
        _todos.value = list
    }

    private fun saveTodos(list: List<Todo>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("isCompleted", item.isCompleted)
                put("createdAt", item.createdAt)
            }
            arr.put(obj)
        }
        prefs.edit().putString("todos_json", arr.toString()).apply()
        _todos.value = list
    }

    fun addTodo(title: String): Todo {
        val todo = Todo(title = title)
        val updated = listOf(todo) + _todos.value
        saveTodos(updated)
        return todo
    }

    fun toggleTodo(id: String) {
        val updated = _todos.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
        saveTodos(updated)
    }

    fun deleteTodo(id: String) {
        val updated = _todos.value.filterNot { it.id == id }
        saveTodos(updated)
    }
}
