package com.mattmayers.todo.tasklist

import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.framework.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class TaskListViewModel @Inject constructor(
        private val taskListRepository: Repository<TaskList>,
        private val taskRepository: Repository<Task>,
        private val schedulerProvider: SchedulerProvider
) : ViewModel<TaskListIntent, TaskListViewState> {
    private val taskListUpdatePublisher: Subject<TaskList> = PublishSubject.create()

    private val intents: Subject<TaskListIntent> = PublishSubject.create()
    private val states: Observable<TaskListViewState> by lazy {
        Observable.merge(
                intents.map { performAction(it) },
                taskListUpdatePublisher.map { DataLoadedAction(it) }
        )
                .scan(TaskListViewState.default, reducer)
    }

    override fun handleIntents(intents: Observable<TaskListIntent>) {
        intents.subscribe(this.intents)
    }

    override fun states(): Observable<TaskListViewState> = states.hide()

    private fun performAction(intent: TaskListIntent): TaskListAction {
        return when (intent) {
            is RefreshDataIntent -> {
                loadData(intent.id)
                        .subscribe(Consumer {
                            taskListUpdatePublisher.onNext(it)
                        })
                StartLoadingAction()
            }
            is CreateTaskIntent -> {
                val task = Task(
                        id = 0L,
                        body = intent.body,
                        completed = false,
                        taskListId = intent.taskListId,
                        notes = null,
                        dueDate = null,
                        lat = null,
                        lng = null
                )
                createTask(task)
                        .flatMap { loadData(intent.taskListId) }
                        .subscribe(Consumer {
                            taskListUpdatePublisher.onNext(it)
                        })
                NoOpAction()
            }
            else -> NoOpAction()
        }
    }

    private fun loadData(id: Long): Single<TaskList> {
        return taskListRepository.getEntity(id)
                .zipWith(taskRepository.getEntityList(id))
                .map { it.first.entity.copy(tasks = it.second.entity) }
                .subscribeOn(schedulerProvider.io())
    }

    private fun createTask(task: Task): Single<Task> {
        return taskRepository.createEntity(task)
                .map { it.entity }
                .subscribeOn(schedulerProvider.io())
    }

    private val reducer: BiFunction<TaskListViewState, TaskListAction, TaskListViewState> =
            BiFunction { previousState, action ->
                when (action) {
                    is StartLoadingAction -> previousState.copy(isLoading = true)
                    is DataLoadedAction -> {
                        previousState.copy(
                                isLoading = false,
                                id = action.taskList.id,
                                title = action.taskList.name,
                                tasks = action.taskList.tasks
                        )
                    }
                    else -> previousState
                }
            }
}