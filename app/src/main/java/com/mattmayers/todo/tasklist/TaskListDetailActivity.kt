package com.mattmayers.todo.tasklist

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding2.view.clicks
import com.mattmayers.todo.R
import com.mattmayers.todo.application.TodoApplication
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.tasklistgroup.CreateTaskListIntent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.task_list_group_detail.*
import javax.inject.Inject

class TaskListDetailActivity : AppCompatActivity() {
    companion object {
        const val ID = "TASK_LIST_ID"
    }

    private val taskListId by lazy { intent.getLongExtra(ID, -1L) }
    private val disposables = CompositeDisposable()

    private val adapter by lazy { TaskAdapter() }

    private val refreshDataPublisher: Subject<RefreshDataIntent> = PublishSubject.create()
    private val createTaskIntentPublisher: Subject<CreateTaskIntent> = PublishSubject.create()
    private val deleteTaskIntentPublisher: Subject<DeleteTaskIntent> = PublishSubject.create()

    @Inject lateinit var viewModel: TaskListViewModel
    @Inject lateinit var schedulerProvider: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.getComponent(this).inject(this)

        setContentView(R.layout.task_list_group_detail)
        setSupportActionBar(toolbar)

        recyclerView.adapter = this.adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.states()
                .observeOn(schedulerProvider.ui())
                .subscribe(this::render)
                .addTo(disposables)
        viewModel.handleIntents(intents())

        fab.clicks()
                .subscribe(fabClickHandler)
                .addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun intents(): Observable<TaskListIntent> = firstLoadIntent()

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