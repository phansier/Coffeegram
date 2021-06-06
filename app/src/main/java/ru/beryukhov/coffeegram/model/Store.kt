package ru.beryukhov.coffeegram.model

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


abstract class Store<Intent : Any, State : Any>(initialState: State) {
    private val _intentChannel: Channel<Intent> = Channel(Channel.UNLIMITED)
    protected val _state = MutableStateFlow(initialState)

    val state: StateFlow<State>
        get() = _state

    fun newIntent(intent: Intent) {
        _intentChannel.trySend(intent).isSuccess
    }

    init {
        GlobalScope.launch {
            handleIntents()
        }
    }

    private suspend fun handleIntents() {
        _intentChannel.consumeAsFlow().collect { _state.value = handleIntent(it) }
    }

    protected abstract fun handleIntent(intent: Intent): State
}

