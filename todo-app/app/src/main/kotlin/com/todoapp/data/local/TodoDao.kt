package com.todoapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.todoapp.data.models.TodoItem
import com.todoapp.data.models.Priority
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY dueDate ASC, priority DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getActiveTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoItem>>

    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 0")
    suspend fun getPendingCount(): Int

    @Insert
    suspend fun insertTodo(todo: TodoItem): Long

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTodoCompletion(id: Int, isCompleted: Boolean)
}