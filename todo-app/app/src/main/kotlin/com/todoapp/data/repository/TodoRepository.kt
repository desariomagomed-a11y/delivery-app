package com.todoapp.data.repository

import com.todoapp.data.local.TodoDao
import com.todoapp.data.models.TodoItem
import com.todoapp.data.models.TodoStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getAllTodos(): Flow<List<TodoItem>> = todoDao.getAllTodos()
    fun getActiveTodos(): Flow<List<TodoItem>> = todoDao.getActiveTodos()
    fun getCompletedTodos(): Flow<List<TodoItem>> = todoDao.getCompletedTodos()

    suspend fun getTodoStats(): TodoStats {
        val total = todoDao.getTotalCount()
        val completed = todoDao.getCompletedCount()
        val pending = todoDao.getPendingCount()
        return TodoStats(
            totalTodos = total,
            completedTodos = completed,
            pendingTodos = pending
        )
    }

    suspend fun addTodo(todo: TodoItem): Long = todoDao.insertTodo(todo)
    suspend fun updateTodo(todo: TodoItem) = todoDao.updateTodo(todo)
    suspend fun deleteTodo(todo: TodoItem) = todoDao.deleteTodo(todo)
    suspend fun toggleTodoCompletion(id: Int, isCompleted: Boolean) = todoDao.updateTodoCompletion(id, isCompleted)
}