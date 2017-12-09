package com.mattmayers.todo.taskdetail

import com.mattmayers.todo.db.model.Task

interface TaskDetailAction

class StartLoadingAction() : TaskDetailAction
class DataLoadedAction(val task: Task) : TaskDetailAction
class TaskSaveAction(val task: Task, val shouldClose: Boolean = false) : TaskDetailAction
class TaskDeleteAction : TaskDetailAction
class NoOpAction : TaskDetailAction