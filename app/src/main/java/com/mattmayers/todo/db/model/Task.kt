package com.mattmayers.todo.db.model

import android.arch.persistence.room.*
import java.util.*

@Entity(tableName = Task.TABLE_NAME,
        foreignKeys = arrayOf(
                ForeignKey(entity = TaskList::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("task_list_id"),
                        onDelete = ForeignKey.CASCADE)
        ))
data class Task(
        @field:PrimaryKey(autoGenerate = true) val id: Long = 0L,
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
    @Ignore constructor(body: String, taskListId: Long) : this(
            body = body,
            taskListId = taskListId,
            completed = false,
            notes = null,
            dueDate = null,
            lat = null,
            lng = null
    )

    companion object {
        const val TABLE_NAME = "tasks"
    }
}