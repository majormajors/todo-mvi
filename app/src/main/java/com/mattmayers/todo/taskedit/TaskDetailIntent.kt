package com.mattmayers.todo.taskedit

import com.mattmayers.todo.framework.UserIntent
import java.util.*

interface TaskDetailIntent : UserIntent
class RefreshDataIntent(val taskId: Long) : TaskDetailIntent

interface TaskEditIntent : TaskDetailIntent
class UpdateTaskBodyIntent(val body: String) : TaskEditIntent
class UpdateTaskCompletedIntent(val completed: Boolean) : TaskEditIntent
class UpdateDueDateIntent(val dueDate: Date) : TaskEditIntent