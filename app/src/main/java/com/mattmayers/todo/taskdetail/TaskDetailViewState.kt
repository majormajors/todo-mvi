package com.mattmayers.todo.taskdetail

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.UIState

data class TaskDetailViewState(
        val isLoading: Boolean = false,
        val shouldClose: Boolean = false,
        val task: Task? = null
) : UIState {
    companion object {
        val default = TaskDetailViewState()
    }
}