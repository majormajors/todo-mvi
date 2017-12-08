package com.mattmayers.todo.tasklistgroup

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
import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.framework.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.task_list_group_detail.*
import javax.inject.Inject

class TaskListGroupDetailActivity : AppCompatActivity() {
    private val taskListGroupId by lazy { intent.getLongExtra(Extra.ID, TaskListGroup.DEFAULT_ID) }
    private val disposables = CompositeDisposable()

    private val refreshDataPublisher: Subject<RefreshDataIntent> = PublishSubject.create()
    private val createTaskListIntentPublisher: Subject<CreateTaskListIntent> = PublishSubject.create()
    private val deleteTaskListIntentPublisher: Subject<DeleteTaskListIntent> = PublishSubject.create()

    private val adapter by lazy { TaskListAdapter() }

    @Inject lateinit var viewModel: TaskListGroupViewModel
    @Inject lateinit var schedulerProvider: SchedulerProvider
    @Inject lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TodoApplication.getComponent(this).inject(this)

        setContentView(R.layout.task_list_group_detail)
        setSupportActionBar(toolbar)

        recyclerView.apply {
            adapter = this@TaskListGroupDetailActivity.adapter
            layoutManager = LinearLayoutManager(this@TaskListGroupDetailActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.apply {
            states().observeOn(schedulerProvider.ui())
                    .subscribe(this@TaskListGroupDetailActivity::render)
                    .addTo(disposables)
            handleIntents(intents())
        }

        fab.clicks()
                .subscribe(fabClickHandler)
                .addTo(disposables)

        adapter.itemClicks()
                .subscribe(router::goToTaskList)
                .addTo(disposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private val fabClickHandler = Consumer<Unit> {
        val textView = EditText(this)
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.add_new_list)
                .setView(textView)
                .setNegativeButton(R.string.cancel, { dialog, _ ->
                    dialog.dismiss()
                })
                .setPositiveButton(R.string.add, { dialog, _ ->
                    createTaskListIntentPublisher.onNext(
                            CreateTaskListIntent(taskListGroupId, textView.text.toString())
                    )
                    dialog.dismiss()
                })
        dialog.show()
    }

    private fun intents(): Observable<TaskListGroupIntent> = firstLoadIntent()
            .mergeWith(createTaskListIntentPublisher)
            .mergeWith(deleteTaskListIntentPublisher)
            .mergeWith(refreshDataPublisher)

    private fun firstLoadIntent(): Observable<TaskListGroupIntent> {
        return Observable.just(RefreshDataIntent(taskListGroupId))
    }

    private fun render(state: TaskListGroupViewState) {
        if (state.isLoading) {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            adapter.taskLists = state.taskLists
            adapter.notifyDataSetChanged()
        }

        title = state.title
    }
}