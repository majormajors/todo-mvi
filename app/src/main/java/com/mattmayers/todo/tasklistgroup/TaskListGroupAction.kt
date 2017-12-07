package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListGroup

interface TaskListGroupAction

class DataLoadedAction(val group: TaskListGroup) : TaskListGroupAction
class StartLoadingAction : TaskListGroupAction
class ViewTaskListAction(val taskList: TaskList) : TaskListGroupAction
class NoOpAction : TaskListGroupAction