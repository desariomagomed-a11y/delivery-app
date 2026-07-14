package com.todoapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

data class TodoStats(
    val totalTodos: Int = 0,
    val completedTodos: Int = 0,
    val pendingTodos: Int = 0
)