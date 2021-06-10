package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class Store<Intent : Any, State : Any>(initialState: State) {
    private val intentFlow = MutableSharedFlow<Intent>()
    protected val stateFlow = MutableStateFlow(initialState)

    val state: StateFlow<State>
        get() = stateFlow

    fun newIntent(intent: Intent) {
        GlobalScope.launch {
            intentFlow.emit(intent)
        }
    }

    init {
        GlobalScope.launch {
            handleIntents()
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private suspend fun handleIntents() {
        intentFlow.collect(collector = object : FlowCollector<Intent> {
            override suspend fun emit(value: Intent) {
                stateFlow.value = handleIntent(value)
            }

        })
    }

    protected abstract fun handleIntent(intent: Intent): State
}

