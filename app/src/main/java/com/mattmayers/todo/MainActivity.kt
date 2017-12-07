package com.mattmayers.todo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mattmayers.todo.application.TodoApplication
import com.mattmayers.todo.db.TodoDatabase
import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.db.model.TaskListGroupDao_Impl
import com.mattmayers.todo.tasklistgroup.TaskListGroupDetailActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var database: TodoDatabase

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(View(this))
        TodoApplication.getComponent(this).inject(this)
    }

    override fun onStart() {
        super.onStart()
        bootstrapDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadTaskListGroup)
                .addTo(disposables)
    }

    override fun onStop() {
        super.onStop()
        disposables.dispose()
    }

    // Hack to ensure we have a default group in the database
    private fun bootstrapDatabase(): Single<Long> {
        val dao = TaskListGroupDao_Impl(database)
        return dao.countAll().flatMap { count ->
            if (count == 0) {
                dao.create(TaskListGroup.default())
            }
            Single.just(TaskListGroup.DEFAULT_ID)
        }
    }

    private val loadTaskListGroup = Consumer<Long> {
        val intent = Intent(this@MainActivity, TaskListGroupDetailActivity::class.java)
        intent.putExtra(TaskListGroupDetailActivity.ID, it)
        startActivity(intent)
        finish()
    }
}
