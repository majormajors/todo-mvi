package com.mattmayers.todo.taskdetail

import android.app.DatePickerDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.editorActionEvents
import com.jakewharton.rxbinding2.widget.textChangeEvents
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
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.task_detail.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskDetailActivity : AppCompatActivity() {
    @Inject lateinit var viewModel: TaskDetailViewModel
    @Inject lateinit var scheduleProvider: SchedulerProvider
    @Inject lateinit var router: Router

    private val disposables = CompositeDisposable()
    private val taskId by lazy { intent.getLongExtra(Extra.ID, -1L) }
    private val taskIntentPublisher = PublishSubject.create<TaskDetailIntent>()
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
                            body.text = it.view().text.toString()
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
                                dueDate.setText(SimpleDateFormat.getDateInstance().format(task?.dueDate))
                                updateDueDate(Date(year - 1900, month, dayOfMonth))
                            },
                            date.year + 1900, date.month, date.date)
                            .show()
                }
                .addTo(disposables)

        location.editorActionEvents()
                .subscribe {
                    if (Geocoder.isPresent() && it.actionId() == EditorInfo.IME_ACTION_DONE) {
                        val geocoder = Geocoder(this@TaskDetailActivity)
                        val results = geocoder.getFromLocationName(it.view().text.toString(), 1)
                        if (results.isNotEmpty()) {
                            updateTaskLocation(results.first())
                            location.setText(AddressRenderer(results.first()).renderSingleLine())
                        }
                    }
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
        taskIntentPublisher.onNext(
                UpdateTaskBodyIntent(bodyEdit.text.toString())
        )
    }

    private fun updateTaskCompleted(isCompleted: Boolean) {
        taskIntentPublisher.onNext(
                UpdateTaskCompletedIntent(isCompleted)
        )
    }

    private fun updateDueDate(date: Date) {
        taskIntentPublisher.onNext(
                UpdateDueDateIntent(date)
        )
    }

    private fun updateTaskLocation(address: Address) {
        taskIntentPublisher.onNext(
                UpdateLocationIntent(address)
        )
    }

    private fun updateTaskNotes(notes: String) {
        taskIntentPublisher.onNext(
                UpdateNotesIntent(notes)
        )
    }

    private fun saveTask(finishActivityAfterSave: Boolean = false) {
        taskIntentPublisher.onNext(
                SaveTaskIntent(finishActivityAfterSave)
        )
    }

    private fun deleteTask() {
        taskIntentPublisher.onNext(DeleteTaskIntent())
    }

    override fun onBackPressed() {
        saveTask(true)
    }

    private fun intents(): Observable<TaskDetailIntent> = firstLoadIntent()
            .mergeWith(taskIntentPublisher)

    private fun firstLoadIntent(): Observable<TaskDetailIntent> =
            Observable.just(RefreshDataIntent(taskId))

    private val notesChangedTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) = Unit
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateTaskNotes(p0.toString())
        }
    }

    private fun render(state: TaskDetailViewState) {
        if (state.shouldClose) {
            finish()
            return
        }
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
            dueDate.setText(SimpleDateFormat.getDateInstance().format(task?.dueDate))
        }
        task?.location?.let {
            location.setText(it)
        }

        notes.apply {
            removeTextChangedListener(notesChangedTextWatcher)
            setText(task?.notes)
            addTextChangedListener(notesChangedTextWatcher)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete) {
            deleteTask()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}