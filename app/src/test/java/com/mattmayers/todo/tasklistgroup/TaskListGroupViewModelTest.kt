package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.BaseViewModelTest
import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.db.repository.TestTaskListGroupRepository
import com.mattmayers.todo.db.repository.TestTaskListRepository
import com.mattmayers.todo.framework.Repository
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class TaskListGroupViewModelTest : BaseViewModelTest<TaskListGroupViewModel>() {
    private lateinit var taskListGroupRepository: Repository<TaskListGroup>
    private lateinit var taskListRepository: Repository<TaskList>

    @Before
    fun setup() {
        taskListGroupRepository = TestTaskListGroupRepository(mutableListOf(
                TaskListGroup(id = 1L, name = "Test Group")
        ))
        taskListRepository = TestTaskListRepository(mutableListOf(
                TaskList(1L, "Task List One", 1L)
        ))
        viewModel = TaskListGroupViewModel(taskListGroupRepository, taskListRepository, schedulerProvider)
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
                    it.title == "Test Group" && it.taskLists.size == 1
                }
    }

    @Test
    fun sendingRefreshDataIntentForInvalidIdGivesError() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(RefreshDataIntent(2L)))
        state.awaitCount(3)
        state.assertError(KotlinNullPointerException::class.java)
                .assertValueAt(0) { !it.isLoading }
                .assertValueAt(1) { it.isLoading }
    }
}