package com.mattmayers.todo.tasklistgroup

import dagger.Subcomponent

@Subcomponent(modules = arrayOf(TaskListGroupDetailComponent.Module::class))
interface TaskListGroupDetailComponent {
    fun inject(activity: TaskListGroupDetailActivity)

    @dagger.Module
    class Module {

    }
}