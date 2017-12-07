package com.mattmayers.todo.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = TaskListGroup.TABLE_NAME)
data class TaskListGroup @JvmOverloads constructor(
        @field:PrimaryKey val id: Long,
        val name: String,
        @field:Ignore val taskLists: List<TaskList> = listOf()
) {

    companion object {
        const val TABLE_NAME = "taskListGroups"
        const val DEFAULT_ID = 1L
        fun default() = TaskListGroup(DEFAULT_ID, "Task Lists")
    }
}