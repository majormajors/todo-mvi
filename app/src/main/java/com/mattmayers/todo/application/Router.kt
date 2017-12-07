package com.mattmayers.todo.application

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskList

/**
 * Created by matt on 12/6/17.
 */
interface Router {
    fun goToTaskList(taskList: TaskList)
    fun goToTask(task: Task)
}