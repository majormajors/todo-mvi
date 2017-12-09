package com.mattmayers.todo.tasklist

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.UserIntent

interface TaskListIntent : UserIntent

class RefreshDataIntent(val id: Long) : TaskListIntent
class UpdateTaskCompletedIntent(val task: Task, val completed: Boolean) : TaskListIntent
class CreateTaskIntent(val taskListId: Long, val body: String) : TaskListIntent
class DeleteTaskIntent(val task: Task) : TaskListIntent