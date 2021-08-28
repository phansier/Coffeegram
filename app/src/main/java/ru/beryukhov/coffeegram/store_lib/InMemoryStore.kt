package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class InMemoryStore<Intent : Any, State : Any>(initialState: State): Store<Intent, State> {
    private val _intentChannel = MutableSharedFlow<Intent>()
    private val _state = MutableStateFlow(initialState)

    override val state: StateFlow<State>
        get() = _state

    override fun newIntent(intent: Intent) {
        runBlocking {
            _intentChannel.emit(intent)
        }
    }

    init {
        GlobalScope.launch {
            handleIntents()
        }
    }

    private suspend fun handleIntents() {
        _intentChannel.collect {
            _state.value = handleIntent(it) }
    }

    protected abstract suspend fun handleIntent(intent: Intent): State
}

