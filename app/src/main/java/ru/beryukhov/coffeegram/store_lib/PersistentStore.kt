package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


abstract class PersistentStore<Intent : Any, State : Any>(initialState: State, private val storage: Storage<State>):
    Store<Intent, State> {
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
            getStoredState()?.let {
                _state.value = it
            }
            handleIntents()
        }
    }

    private suspend fun handleIntents() {
        _intentChannel.collect {
            _state.value = handleIntent(it)
            storage.saveState(_state.value)
        }
    }

    private suspend fun getStoredState(): State? {
        return storage.getState()
    }

    protected abstract fun handleIntent(intent: Intent): State
}

