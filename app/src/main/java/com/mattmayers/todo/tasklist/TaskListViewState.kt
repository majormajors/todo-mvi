package com.mattmayers.todo.tasklist

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.UIState

data class TaskListViewState(
        val isLoading: Boolean = false,
        val id: Long = 0L,
        val title: String = "",
        val tasks: List<Task> = listOf()
) : UIState {
    companion object {
        val default = TaskListViewState()
    }
}