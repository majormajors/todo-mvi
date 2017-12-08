package com.mattmayers.todo.tasklist

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding2.view.clicks
import com.mattmayers.todo.R
import com.mattmayers.todo.application.Extra
import com.mattmayers.todo.application.Router
import com.mattmayers.todo.application.TodoApplication
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.task_list_group_detail.*
import javax.inject.Inject

class TaskListDetailActivity : AppCompatActivity() {
    private val taskListId by lazy { intent.getLongExtra(Extra.ID, 0L) }
    private val disposables = CompositeDisposable()

    private val adapter by lazy { TaskAdapter() }

    private val refreshDataPublisher: Subject<RefreshDataIntent> = PublishSubject.create()
    private val updateTaskCompletedStatePublisher: Subject<UpdateTaskCompletedIntent> = PublishSubject.create()
    private val createTaskIntentPublisher: Subject<CreateTaskIntent> = PublishSubject.create()
    private val updateTaskIntentPublisher: Subject<UpdateTaskIntent> = PublishSubject.create()
    private val deleteTaskIntentPublisher: Subject<DeleteTaskIntent> = PublishSubject.create()

    @Inject lateinit var viewModel: TaskListViewModel
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.getComponent(this).inject(this)

        setContentView(R.layout.task_list_group_detail)
        setSupportActionBar(toolbar)

        recyclerView.apply {
            adapter = this@TaskListDetailActivity.adapter
            layoutManager = LinearLayoutManager(this@TaskListDetailActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.apply {
            states().observeOn(schedulerProvider.ui())
                    .subscribe(this@TaskListDetailActivity::render)
                    .addTo(disposables)

            handleIntents(intents())
        }

        fab.clicks()
                .observeOn(schedulerProvider.ui())
                .subscribe(fabClickHandler)
                .addTo(disposables)

        adapter.itemClicks()
                .observeOn(schedulerProvider.ui())
                .subscribe(router::goToEditTask)
                .addTo(disposables)

        adapter.itemCheckChanges()
                .observeOn(schedulerProvider.ui())
                .subscribe(itemCheckChangeHandler)
                .addTo(disposables)
    }

    override fun onResume() {
        super.onResume()
        refreshDataPublisher.onNext(RefreshDataIntent(taskListId))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun intents(): Observable<TaskListIntent> = firstLoadIntent()
            .mergeWith(createTaskIntentPublisher)
            .mergeWith(deleteTaskIntentPublisher)
            .mergeWith(refreshDataPublisher)
            .mergeWith(updateTaskCompletedStatePublisher)
            .mergeWith(updateTaskIntentPublisher)

    private fun firstLoadIntent() : Observable<TaskListIntent> {
        return Observable.just(RefreshDataIntent(taskListId))
    }

    private val fabClickHandler = Consumer<Unit> {
        val textView = EditText(this)
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.add_new_task)
                .setView(textView)
                .setNegativeButton(R.string.cancel, { dialog, _ ->
                    dialog.dismiss()
                })
                .setPositiveButton(R.string.add, { dialog, _ ->
                    createTaskIntentPublisher.onNext(
                            CreateTaskIntent(taskListId, textView.text.toString())
                    )
                    dialog.dismiss()
                })
        dialog.show()
    }

    private val itemCheckChangeHandler = Consumer<Pair<Task, Boolean>> {
        val (task, isCompleted) = it
        updateTaskCompletedStatePublisher.onNext(UpdateTaskCompletedIntent(task, isCompleted))
    }

    private fun render(state: TaskListViewState) {
        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        title = state.title
        adapter.tasks = state.tasks
        adapter.notifyDataSetChanged()
    }
}