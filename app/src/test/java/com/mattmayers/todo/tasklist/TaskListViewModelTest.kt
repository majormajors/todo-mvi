package com.mattmayers.todo.tasklist

import com.mattmayers.todo.BaseViewModelTest
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.repository.TestTaskListRepository
import com.mattmayers.todo.db.repository.TestTaskRepository
import com.mattmayers.todo.framework.Repository
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class TaskListViewModelTest : BaseViewModelTest<TaskListViewModel>() {
    private lateinit var taskListRepository: Repository<TaskList>
    private lateinit var taskRepository: Repository<Task>

    private val taskList = TaskList(1L, "Task List One", 1L)
    private val task = Task(1L, "This is a task", false, null, null, null, null, null, 1L)

    @Before
    fun setup() {
        taskListRepository = TestTaskListRepository(mutableListOf(taskList))
        taskRepository = TestTaskRepository(mutableListOf(task))
        viewModel = TaskListViewModel(taskListRepository, taskRepository, schedulerProvider)
    }

    @Test
    fun sendingRefreshDataIntentReturnsRelevantData() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(RefreshDataIntent(1L)))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertValueAt(0) { !it.isLoading }
                .assertValueAt(1) { it.isLoading }
                .assertValueAt(2) {
                    it.title == "Task List One" && it.tasks.size == 1
                }
    }

    @Test
    fun createTaskIntentAddsNewTask() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(CreateTaskIntent(1L, "This is another task!")))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertValueAt(2) {
                    it.tasks[1].body == "This is another task!"
                }
    }

    @Test
    fun updateTaskCompletedIntentUpdatesTheTask() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(UpdateTaskCompletedIntent(task, true)))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertValueAt(2) {
                    it.tasks[0].completed
                }
    }
}