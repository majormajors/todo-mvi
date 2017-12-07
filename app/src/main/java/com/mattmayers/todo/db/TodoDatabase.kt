package com.mattmayers.todo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.mattmayers.todo.db.model.*

@Database(
        entities = arrayOf(
            TaskListGroup::class,
            TaskList::class,
            Task::class
        ), version = TodoDatabase.VERSION)
@TypeConverters(DateTypeConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        const val VERSION = 1
    }

    abstract fun taskListGroupDao(): TaskListGroupDao
    abstract fun taskListsDao(): TaskListDao
    abstract fun taskDao(): TaskDao
}