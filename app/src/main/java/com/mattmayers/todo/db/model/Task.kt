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
        @field:PrimaryKey(autoGenerate = true) override val id: Long = 0L,
        val body: String,
        val completed: Boolean,
        val notes: String?,
        @field:ColumnInfo(name = "due_date")
        val dueDate: Date?,
        val location: String?,
        val lat: Double?,
        val lng: Double?,
        @field:ColumnInfo(name = "task_list_id")
        val taskListId: Long?
): DbModel {
    @Ignore constructor(body: String, taskListId: Long) : this(
            body = body,
            taskListId = taskListId,
            completed = false,
            notes = null,
            dueDate = null,
            location = null,
            lat = null,
            lng = null
    )

    companion object {
        const val TABLE_NAME = "tasks"
    }
}