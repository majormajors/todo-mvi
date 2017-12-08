package com.mattmayers.todo.taskedit

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.framework.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class TaskDetailViewModel @Inject constructor(
        private val taskRepository: Repository<Task>,
        private val schedulerProvider: SchedulerProvider
) : ViewModel<TaskDetailIntent, TaskDetailViewState> {
    private val taskUpdatePublisher: Subject<Task> = PublishSubject.create()

    private val intents: Subject<TaskDetailIntent> = PublishSubject.create()
    private val states: Observable<TaskDetailViewState> by lazy {
        Observable.merge(
                intents.map { performAction(it) },
                taskUpdatePublisher
                        .doOnNext { this@TaskDetailViewModel.task = it }
                        .map { DataLoadedAction(it) }
        )
                .scan(TaskDetailViewState.default, reducer)
    }

    private var task: Task? = null

    override fun handleIntents(intents: Observable<TaskDetailIntent>) {
        intents.subscribe(this.intents)
    }

    override fun states(): Observable<TaskDetailViewState> = states.hide()

    private fun performAction(intent: TaskDetailIntent): TaskDetailAction {
        return when (intent) {
            is RefreshDataIntent -> {
                loadData(intent.taskId)
                        .subscribe(Consumer {
                            taskUpdatePublisher.onNext(it)
                        })
                StartLoadingAction()
            }
            is TaskEditIntent -> {
                updateTask(intent)
                        .subscribe(Consumer {
                            taskUpdatePublisher.onNext(it)
                        })
                NoOpAction()
            }
            else -> NoOpAction()
        }
    }

    private fun loadData(id: Long): Single<Task> {
        return taskRepository.getEntity(id)
                .map { it.entity }
                .subscribeOn(schedulerProvider.io())
    }

    private fun updateTask(intent: TaskEditIntent): Single<Task> {
        val task = when (intent) {
            is UpdateTaskBodyIntent -> task?.copy(body = intent.body)
            is UpdateTaskCompletedIntent -> task?.copy(completed = intent.completed)
            is UpdateDueDateIntent -> task?.copy(dueDate = intent.dueDate)
            else -> throw IllegalArgumentException()
        }
        return taskRepository.updateEntity(task ?: throw IllegalStateException())
                .map { it.entity }
                .subscribeOn(schedulerProvider.io())
    }

    private val reducer = BiFunction<TaskDetailViewState, TaskDetailAction, TaskDetailViewState>
    { previousState, action ->
        when (action) {
            is StartLoadingAction -> previousState.copy(isLoading = true)
            is DataLoadedAction -> previousState.copy(isLoading = false, task = action.task)
            else -> previousState
        }
    }
}