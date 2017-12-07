package com.mattmayers.todo.tasklist

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.UserIntent

interface TaskListIntent : UserIntent

class RefreshDataIntent(val id: Long) : TaskListIntent
class CompleteTaskIntent(val task: Task) : TaskListIntent
class UncompleteTaskIntent(val task: Task) : TaskListIntent
class CreateTaskIntent(val taskListId: Long, val body: String) : TaskListIntent
class DeleteTaskIntent(val task: Task) : TaskListIntent