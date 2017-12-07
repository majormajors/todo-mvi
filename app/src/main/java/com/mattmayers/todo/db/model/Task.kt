package com.mattmayers.todo.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = Task.TABLE_NAME,
        foreignKeys = arrayOf(
                ForeignKey(entity = TaskList::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("task_list_id"),
                        onDelete = ForeignKey.CASCADE)
        ))
data class Task(
        @field:PrimaryKey val id: Long,
        val body: String,
        val completed: Boolean,
        val notes: String?,
        @field:ColumnInfo(name = "due_date")
        val dueDate: Date?,
        val lat: Double?,
        val lng: Double?,
        @field:ColumnInfo(name = "task_list_id")
        val taskListId: Long?
) {
    companion object {
        const val TABLE_NAME = "tasks"
    }
}