package com.mattmayers.todo.taskedit

import com.mattmayers.todo.db.model.Task

interface TaskDetailAction

class StartLoadingAction() : TaskDetailAction
class DataLoadedAction(val task: Task) : TaskDetailAction
class NoOpAction : TaskDetailAction