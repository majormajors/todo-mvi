package com.mattmayers.todo.taskdetail

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
    private val dataLoadedPublisher: Subject<Task> = PublishSubject.create()
    private val taskDeletePublisher: Subject<Task> = PublishSubject.create()
    private val taskSavePublisher: Subject<Pair<Task, Boolean>> = PublishSubject.create()

    private val intents: Subject<TaskDetailIntent> = PublishSubject.create()
    private val states: Observable<TaskDetailViewState> by lazy {
        Observable.merge(
                intents.map { performAction(it) }
                        .filter { it !is NoOpAction },
                dataLoadedPublisher
                        .doOnNext { this@TaskDetailViewModel.task = it }
                        .map { DataLoadedAction(it) },
                taskSavePublisher
                        .doOnNext { this@TaskDetailViewModel.task = it.first }
                        .map { TaskSaveAction(it.first, it.second) },
                taskDeletePublisher
                        .map { TaskDeleteAction() }
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
                            dataLoadedPublisher.onNext(it)
                        })
                StartLoadingAction()
            }
            is TaskEditIntent -> {
                updateTask(intent)
                        .doOnSuccess { this@TaskDetailViewModel.task = task }
                        .subscribe()
                NoOpAction()
            }
            is SaveTaskIntent -> {
                taskRepository.updateEntity(task ?: throw IllegalStateException())
                        .map { it.entity }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe(Consumer {
                            taskSavePublisher.onNext(Pair(it, intent.finishActivityAfterSave))
                        })
                NoOpAction()
            }
            is DeleteTaskIntent -> {
                taskRepository.deleteEntity(task ?: throw IllegalStateException())
                        .subscribeOn(schedulerProvider.io())
                        .map { it.entity }
                        .subscribe(Consumer {
                            taskDeletePublisher.onNext(it)
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
        this.task = when (intent) {
            is UpdateTaskBodyIntent -> task?.copy(body = intent.body)
            is UpdateTaskCompletedIntent -> task?.copy(completed = intent.completed)
            is UpdateDueDateIntent -> task?.copy(dueDate = intent.dueDate)
            is UpdateLocationIntent -> {
                val address = intent.address
                task?.copy(
                        location = AddressRenderer(address).renderSingleLine(),
                        lat = address.latitude,
                        lng = address.longitude
                )
            }
            is UpdateNotesIntent -> task?.copy(notes = intent.notes)
            else -> throw IllegalArgumentException()
        }
        return Single.just(this.task)
    }

    private val reducer = BiFunction<TaskDetailViewState, TaskDetailAction, TaskDetailViewState>
    { previousState, action ->
        when (action) {
            is StartLoadingAction -> previousState.copy(isLoading = true)
            is DataLoadedAction -> previousState.copy(isLoading = false, task = action.task)
            is TaskSaveAction -> previousState.copy(shouldClose = action.shouldClose, isLoading = false, task = action.task)
            is TaskDeleteAction -> previousState.copy(shouldClose = true)
            else -> previousState
        }
    }
}