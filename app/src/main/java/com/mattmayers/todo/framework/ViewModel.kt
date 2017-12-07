package com.mattmayers.todo.framework

import io.reactivex.Observable

interface ViewModel<I : UserIntent, S : UIState> {
    fun handleIntents(intents: Observable<I>)
    fun states(): Observable<S>
}