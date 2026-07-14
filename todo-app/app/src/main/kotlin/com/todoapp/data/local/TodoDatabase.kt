package com.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.todoapp.data.models.TodoItem

@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}