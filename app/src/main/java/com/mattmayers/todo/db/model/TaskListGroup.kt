package com.mattmayers.todo.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = TaskListGroup.TABLE_NAME)
data class TaskListGroup @Ignore constructor(
        @field:PrimaryKey(autoGenerate = true) val id: Long = 0L,
        val name: String,
        @field:Ignore val taskLists: List<TaskList> = listOf()
) {
    constructor(id: Long, name: String): this(id, name, listOf())

    companion object {
        const val TABLE_NAME = "taskListGroups"
        const val DEFAULT_ID = 1L
        fun default() = TaskListGroup(name = "Task Lists")
    }
}