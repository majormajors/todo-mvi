package com.mattmayers.todo.application

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import com.mattmayers.todo.MainActivity
import com.mattmayers.todo.db.TodoDatabase
import com.mattmayers.todo.db.model.Task
import com.mattmayers.todo.db.model.TaskList
import com.mattmayers.todo.db.model.TaskListGroup
import com.mattmayers.todo.db.repository.TaskListGroupRepository
import com.mattmayers.todo.db.repository.TaskListRepository
import com.mattmayers.todo.db.repository.TaskRepository
import com.mattmayers.todo.framework.Repository
import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.tasklist.TaskListDetailActivity
import com.mattmayers.todo.tasklistgroup.TaskListGroupDetailActivity
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

class TodoApplication : Application() {
    companion object {
        fun getComponent(context: Context) = (context.applicationContext as TodoApplication).component
    }

    val component: Component by lazy {
        DaggerTodoApplication_Component.builder().module(Module(this)).build()
    }

    @dagger.Component(modules = arrayOf(Module::class))
    @Singleton
    interface Component {
        fun inject(activity: MainActivity)
        fun inject(activity: TaskListGroupDetailActivity)
        fun inject(activity: TaskListDetailActivity)
    }

    @dagger.Module
    class Module(context: Context) {
        private val applicationContext = context.applicationContext

        @Provides
        @Singleton
        fun provideContext(): Context = applicationContext

        @Provides
        @Singleton
        fun provideTodoDatabase(): TodoDatabase = Room
                .databaseBuilder(applicationContext, TodoDatabase::class.java, "todo-database")
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        @Singleton
        fun providesSchedulerProvider(): SchedulerProvider = object : SchedulerProvider {
            override fun io(): Scheduler = Schedulers.io()
            override fun ui(): Scheduler = AndroidSchedulers.mainThread()
        }

        @Provides
        @Singleton
        fun provideRouter(): Router = object : Router {
            override fun goToTaskList(taskList: TaskList) {
                val intent = Intent(applicationContext, TaskListDetailActivity::class.java)
                intent.putExtra(TaskListDetailActivity.ID, taskList.id)
                applicationContext.startActivity(intent)
            }

            override fun goToTask(task: Task) {
            }
        }

        @Provides
        fun provideTaskListGroupRepository(database: TodoDatabase): Repository<TaskListGroup> = TaskListGroupRepository(database.taskListGroupDao())


        @Provides
        fun provideTaskListRepository(database: TodoDatabase): Repository<TaskList> = TaskListRepository(database.taskListsDao())

        @Provides
        fun provideTaskRepository(database: TodoDatabase): Repository<Task> = TaskRepository(database.taskDao())
    }
}