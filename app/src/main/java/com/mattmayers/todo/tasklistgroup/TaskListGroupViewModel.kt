package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListGroup
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

class TaskListGroupViewModel @Inject constructor(
        private val taskListGroupsRepo: Repository<TaskListGroup>,
        private val taskListsRepo: Repository<TaskList>,
        private val schedulerProvider: SchedulerProvider
): ViewModel<TaskListGroupIntent, TaskListGroupViewState> {
    private val taskListGroupUpdatePublisher: Subject<TaskListGroup> = PublishSubject.create()

    private val intents: Subject<TaskListGroupIntent> = PublishSubject.create()
    private val states: Observable<TaskListGroupViewState> by lazy {
        Observable.merge(
                intents.map { performAction(it) },
                taskListGroupUpdatePublisher.map { DataLoadedAction(it) }
        )
                .scan(TaskListGroupViewState.default, reducer)
    }

    override fun handleIntents(intents: Observable<TaskListGroupIntent>) = intents.subscribe(this.intents)
    override fun states(): Observable<TaskListGroupViewState> = states.hide()

    private fun performAction(intent: TaskListGroupIntent): TaskListGroupAction {
        return when (intent) {
            is RefreshDataIntent -> {
                loadData(intent.id)
                        .observeOn(schedulerProvider.ui())
                        .subscribe({
                            taskListGroupUpdatePublisher.onNext(it)
                        },{
                            taskListGroupUpdatePublisher.onError(it)
                        })
                StartLoadingAction()
            }
            is CreateTaskListIntent -> {
                val entity = TaskList(
                        name = intent.title,
                        taskListGroupId = intent.groupId
                )
                createTaskList(entity)
                        .flatMap { loadData(entity.taskListGroupId) }
                        .observeOn(schedulerProvider.ui())
                        .subscribe({
                            taskListGroupUpdatePublisher.onNext(it)
                        },{
                            taskListGroupUpdatePublisher.onError(it)
                        })
                NoOpAction()
            }
            else -> NoOpAction()
        }
    }

    private fun loadData(id: Long): Single<TaskListGroup> {
        return taskListGroupsRepo.getEntity(id)
                .zipWith(taskListsRepo.getEntityList(id))
                .map { it.first.entity.copy(taskLists = it.second.entity) }
                .subscribeOn(schedulerProvider.io())
    }

    private fun createTaskList(taskList: TaskList): Single<TaskList> {
        return taskListsRepo.createEntity(taskList)
                .map { it.entity }
                .subscribeOn(schedulerProvider.io())
    }

    private val reducer: BiFunction<TaskListGroupViewState, TaskListGroupAction, TaskListGroupViewState> =
            BiFunction { previousState, action ->
                when (action) {
                    is StartLoadingAction -> previousState.copy(isLoading = true)
                    is DataLoadedAction -> {
                        previousState.copy(
                                isLoading = false,
                                id = action.group.id,
                                title = action.group.name,
                                taskLists = action.group.taskLists
                        )
                    }
                    else -> previousState
                }
            }
}