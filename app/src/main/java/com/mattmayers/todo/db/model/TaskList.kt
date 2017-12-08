package com.mattmayers.todo.db.model

import android.arch.persistence.room.*

@Entity(
        tableName = TaskList.TABLE_NAME,
        foreignKeys = arrayOf(ForeignKey(entity = TaskListGroup::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("task_list_group_id"),
                onDelete = ForeignKey.CASCADE))
)
data class TaskList @Ignore constructor(
        @field:PrimaryKey(autoGenerate = true)
        override val id: Long = 0L,
        val name: String,
        @field:ColumnInfo(name = "task_list_group_id")
        val taskListGroupId: Long,
        @field:Ignore val tasks: List<Task> = listOf()
): DbModel {
    constructor(id: Long = 0, name: String, taskListGroupId: Long): this(id, name, taskListGroupId, listOf())

    companion object {
        const val TABLE_NAME = "taskLists"
    }
}