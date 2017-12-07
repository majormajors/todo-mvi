package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.framework.UserIntent

interface TaskListGroupIntent : UserIntent

class RefreshDataIntent(val id: Long) : TaskListGroupIntent
class CreateTaskListIntent(val groupId: Long, val title: String) : TaskListGroupIntent
class DeleteTaskListIntent : TaskListGroupIntent
class ViewTaskListIntent(val taskList: TaskList) : TaskListGroupIntent