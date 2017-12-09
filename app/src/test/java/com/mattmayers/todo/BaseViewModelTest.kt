package com.mattmayers.todo

import com.mattmayers.todo.framework.SchedulerProvider

import com.mattmayers.todo.framework.ViewModel
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.Ignore

@Ignore
open class BaseViewModelTest<T : ViewModel<*,*>> {
    lateinit var viewModel: T

    private val testScheduler: Scheduler = Schedulers.single()
    val schedulerProvider = object : SchedulerProvider {
        override fun io(): Scheduler = testScheduler
        override fun ui(): Scheduler = testScheduler
    }
}