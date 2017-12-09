package com.mattmayers.todo.taskdetail

import com.mattmayers.todo.BaseViewModelTest
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.repository.TestTaskRepository
import com.mattmayers.todo.framework.Repository
import io.reactivex.Observable
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class TaskDetailViewModelTest : BaseViewModelTest<TaskDetailViewModel>() {
    private lateinit var taskRepository: Repository<Task>

    private val task = Task(1L, "This is a task", false, null, null, null, null, null, 1L)

    @Before
    fun setup() {
        taskRepository = TestTaskRepository(mutableListOf(task))
        viewModel = TaskDetailViewModel(taskRepository, schedulerProvider)
        viewModel.task = task
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
                    it.task == task
                }
    }

    @Test
    fun deleteTaskIntentDeletesTheTask() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                RefreshDataIntent(1L),
                DeleteTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
        Assert.assertEquals(0,
                taskRepository.getEntityList(1L).blockingGet().entity.size)
    }

    @Test
    fun updateBodyFollowedBySaveWorks() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                UpdateTaskBodyIntent("New body"),
                SaveTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertOf {
                    "New body" == it.values().last().task?.body
                }
    }

    @Test
    fun updateCompletedFollowedBySaveWorks() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                UpdateTaskCompletedIntent(true),
                SaveTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertOf {
                    it.values().last().task?.completed
                }
    }

    @Test
    fun updateDueDateFollowedBySaveWorks() {
        val date = Date()
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                UpdateDueDateIntent(date),
                SaveTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertOf {
                    date == it.values().last().task?.dueDate
                }
    }

    @Test
    fun updateLocationFollowedBySaveWorks() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                UpdateLocationIntent("123 Main Street", 37.7749, -122.4194),
                SaveTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertOf {
                    "123 Main Street" == it.values().last().task?.location
                }
    }

    @Test
    fun updateNotesFollowedBySaveWorks() {
        val state = viewModel.states().test()
        viewModel.handleIntents(Observable.just(
                UpdateNotesIntent("Notes go here"),
                SaveTaskIntent()
        ))
        state.awaitCount(3)
        state.assertNoErrors()
                .assertOf {
                    "Notes go here" == it.values().last().task?.notes
                }
    }
}