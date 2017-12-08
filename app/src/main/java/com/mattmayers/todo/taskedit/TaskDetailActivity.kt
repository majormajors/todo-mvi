package com.mattmayers.todo.taskedit

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.editorActionEvents
import com.mattmayers.todo.R
import com.mattmayers.todo.application.Extra
import com.mattmayers.todo.application.Router
import com.mattmayers.todo.application.TodoApplication
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.kext.focusAndShowSoftKeyboard
import com.mattmayers.todo.kext.hideSoftKeyboard
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.task_detail.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TaskDetailActivity : AppCompatActivity() {
    @Inject lateinit var viewModel: TaskDetailViewModel
    @Inject lateinit var scheduleProvider: SchedulerProvider
    @Inject lateinit var router: Router

    private val disposables = CompositeDisposable()
    private val taskId by lazy { intent.getLongExtra(Extra.ID, -1L) }
    private val taskEditIntentPublisher = PublishSubject.create<TaskEditIntent>()
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_detail)
        setSupportActionBar(toolbar)
        title = ""
        collapsingToolbarLayout.title = ""

        TodoApplication.getComponent(this).inject(this)

        viewModel.apply {
            states().observeOn(scheduleProvider.ui())
                    .subscribe(this@TaskDetailActivity::render)
                    .addTo(disposables)

            handleIntents(intents())
        }

        body.clicks()
                .subscribe { makeBodyEditable() }
                .addTo(disposables)
        bodyEdit.editorActionEvents()
                .subscribe {
                    when (it.actionId()) {
                        EditorInfo.IME_ACTION_DONE ->  {
                            updateTaskBody()
                            makeBodyNonEditable()
                        }
                    }
                }
                .addTo(disposables)
        dueDate.clicks()
                .subscribe {
                    val date = task?.dueDate ?: Date()
                    DatePickerDialog(this@TaskDetailActivity,
                            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                                updateDueDate(Date(year - 1900, month, dayOfMonth))
                            },
                            date.year + 1900, date.month, date.date)
                            .show()
                }
                .addTo(disposables)
    }

    private fun makeBodyEditable() {
        body.visibility = View.GONE
        bodyEdit.visibility = View.VISIBLE
        bodyEdit.focusAndShowSoftKeyboard()
        bodyEdit.setSelection(body.text.length)
    }

    private fun makeBodyNonEditable() {
        bodyEdit.visibility = View.GONE
        body.visibility = View.VISIBLE
        bodyEdit.hideSoftKeyboard()
    }

    private fun updateTaskBody() {
        taskEditIntentPublisher.onNext(
                UpdateTaskBodyIntent(bodyEdit.text.toString())
        )
    }

    private fun updateTaskCompleted(isCompleted: Boolean) {
        taskEditIntentPublisher.onNext(
                UpdateTaskCompletedIntent(isCompleted)
        )
    }

    private fun updateDueDate(date: Date) {
        taskEditIntentPublisher.onNext(
                UpdateDueDateIntent(date)
        )
    }

    private fun intents(): Observable<TaskDetailIntent> = firstLoadIntent()
            .mergeWith(taskEditIntentPublisher)

    private fun firstLoadIntent(): Observable<TaskDetailIntent> =
            Observable.just(RefreshDataIntent(taskId))

    private fun render(state: TaskDetailViewState) {
        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }

        task = state.task

        body.text = task?.body
        body.visibility = View.VISIBLE
        bodyEdit.setText(task?.body)
        bodyEdit.visibility = View.GONE
        completedCheckbox.apply {
            setOnCheckedChangeListener(null)
            isChecked = task?.completed ?: false
            setOnCheckedChangeListener { _, isChecked -> updateTaskCompleted(isChecked) }
        }
        task?.dueDate?.let {
            dueDate.text = SimpleDateFormat.getDateInstance().format(task?.dueDate)
        }
        notes.text = task?.notes
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_detail_menu, menu)
        return true
    }
}