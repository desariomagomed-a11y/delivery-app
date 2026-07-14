package com.todoapp.di

import android.content.Context
import androidx.room.Room
import com.todoapp.data.local.TodoDatabase
import com.todoapp.data.local.TodoDao
import com.todoapp.data.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(context, TodoDatabase::class.java, "todo_database").build()
    }

    @Singleton
    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao = database.todoDao()

    @Singleton
    @Provides
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository = TodoRepository(todoDao)
}