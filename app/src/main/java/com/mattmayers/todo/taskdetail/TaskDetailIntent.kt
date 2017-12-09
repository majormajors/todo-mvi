package com.mattmayers.todo.taskdetail

import com.google.android.gms.location.places.Place
import com.mattmayers.todo.framework.UserIntent
import java.util.*

interface TaskDetailIntent : UserIntent
class RefreshDataIntent(val taskId: Long) : TaskDetailIntent
class SaveTaskIntent(val finishActivityAfterSave: Boolean = false) : TaskDetailIntent
class DeleteTaskIntent : TaskDetailIntent

interface TaskEditIntent : TaskDetailIntent
class UpdateTaskBodyIntent(val body: String) : TaskEditIntent
class UpdateTaskCompletedIntent(val completed: Boolean) : TaskEditIntent
class UpdateDueDateIntent(val dueDate: Date) : TaskEditIntent
class UpdateLocationIntent(val place: Place) : TaskEditIntent
class UpdateNotesIntent(val notes: String) : TaskEditIntent