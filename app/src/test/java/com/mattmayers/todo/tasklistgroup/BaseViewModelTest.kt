package com.mattmayers.todo.tasklistgroup

import com.mattmayers.todo.framework.SchedulerProvider
import com.mattmayers.todo.framework.UIState
import com.mattmayers.todo.framework.UserIntent
import com.mattmayers.todo.framework.ViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Ignore

@Ignore
open class BaseViewModelTest<T : ViewModel<I, S>, I : UserIntent, S : UIState> {
    lateinit var viewModel: T

    private val testScheduler: Scheduler = Schedulers.single()
    val schedulerProvider = object : SchedulerProvider {
        override fun io(): Scheduler = testScheduler
        override fun ui(): Scheduler = testScheduler
    }

//    fun setupIntentPipeline(intents: Observable<I>, replayBufferSize: Int = 1): Observable<S> {
//        val stateReceiver = viewModel.states()
//        viewModel.handleIntents(intents)
//        return stateReceiver
//    }
}