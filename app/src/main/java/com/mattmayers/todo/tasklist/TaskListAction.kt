package com.mattmayers.todo.tasklist

import com.mattmayers.todo.db.model.TaskList

interface TaskListAction

class StartLoadingAction : TaskListAction
class DataLoadedAction(val taskList: TaskList) : TaskListAction
class NoOpAction : TaskListAction