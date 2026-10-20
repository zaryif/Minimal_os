package com.zaryif.minimalos.domain.model

data class Note(
    val id: String = System.currentTimeMillis().toString(),
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class Todo(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
