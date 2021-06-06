package ru.beryukhov.coffeegram.model

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


abstract class Store<Intent : Any, State : Any>(initialState: State) {
    private val _intentChannel = MutableSharedFlow<Intent>()//: Channel<Intent> = Channel(Channel.UNLIMITED)
    protected val _state = MutableStateFlow(initialState)

    val state: StateFlow<State>
        get() = _state

    fun newIntent(intent: Intent) {
        runBlocking {
            _intentChannel.emit(intent)//.offer(intent)
        }
    }

    init {
        GlobalScope.launch {
            handleIntents()
        }
    }

    private suspend fun handleIntents() {
        _intentChannel.collect {
            Log.d("TEST_", "$it")
            _state.value = handleIntent(it) }
    }

    protected abstract fun handleIntent(intent: Intent): State
}

