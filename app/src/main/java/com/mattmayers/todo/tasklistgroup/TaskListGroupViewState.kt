package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.framework.UIState

data class TaskListGroupViewState(
        val isLoading: Boolean = false,
        val newTaskList: Boolean = false,
        val dataUpdated: Boolean = false,
        val id: Long = -1L,
        val title: String = "",
        val taskLists: List<TaskList> = listOf()
) : UIState {
    companion object {
        val default = TaskListGroupViewState()
    }
}