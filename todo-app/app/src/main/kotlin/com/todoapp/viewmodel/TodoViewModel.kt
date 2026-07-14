package com.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.data.models.Priority
import com.todoapp.data.models.TodoItem
import com.todoapp.data.models.TodoStats
import com.todoapp.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _todoStats = MutableStateFlow(TodoStats())
    val todoStats = _todoStats.asStateFlow()

    private val _filter = MutableStateFlow("all")
    val filter = _filter.asStateFlow()

    val todos: Flow<List<TodoItem>>
        get() = when (_filter.value) {
            "active" -> repository.getActiveTodos()
            "completed" -> repository.getCompletedTodos()
            else -> repository.getAllTodos()
        }

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _todoStats.emit(repository.getTodoStats())
        }
    }

    fun setFilter(filter: String) {
        _filter.value = filter
    }

    fun addTodo(title: String, description: String, priority: Priority) {
        viewModelScope.launch {
            val todo = TodoItem(title = title, description = description, priority = priority)
            repository.addTodo(todo)
            loadStats()
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(updatedAt = System.currentTimeMillis()))
            loadStats()
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            loadStats()
        }
    }

    fun toggleTodo(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTodoCompletion(id, !isCompleted)
            loadStats()
        }
    }
}