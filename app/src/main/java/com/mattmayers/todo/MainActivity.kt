package com.mattmayers.todo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mattmayers.todo.application.Extra
import com.mattmayers.todo.application.TodoApplication
import com.mattmayers.todo.db.TodoDatabase
import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.tasklistgroup.TaskListGroupDetailActivity
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo

import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var database: TodoDatabase
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View(this))
        TodoApplication.getComponent(this).inject(this)
    }

    override fun onResume() {
        super.onResume()
        bootstrapDatabase()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(loadTaskListGroup)
                .addTo(disposables)
    }

    override fun onPause() {
        super.onPause()
        disposables.dispose()
    }

    // Hack to ensure we have a default group in the database
    private fun bootstrapDatabase(): Single<Long> {
        val dao = database.taskListGroupDao()
        return dao.countAll().flatMap { count ->
            if (count == 0) {
                dao.create(TaskListGroup.default())
            }
            dao.findFirstId()
        }
    }

    private val loadTaskListGroup = Consumer<Long> {
        val intent = Intent(this@MainActivity, TaskListGroupDetailActivity::class.java)
        intent.putExtra(Extra.ID, it)
        startActivity(intent)
        finish()
    }
}
