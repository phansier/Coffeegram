package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class PersistentStore<Intent : Any, State : Any>(initialState: State, private val storage: Storage<State>) :
    Store<Intent, State> {
    private val intentFlow = MutableSharedFlow<Intent>()
    private val stateFlow = MutableStateFlow(initialState)

    override val state: StateFlow<State>
        get() = stateFlow

    init {
        GlobalScope.launch {
            getStoredState()?.let {
                stateFlow.value = it
            }
            handleIntents()
        }
    }

    override fun newIntent(intent: Intent) {
        GlobalScope.launch {
            intentFlow.emit(intent)
        }
    }

    private suspend fun handleIntents() {
        intentFlow.collect {
            stateFlow.value = handleIntent(it)
            storage.saveState(stateFlow.value)
        }
    }

    private suspend fun getStoredState(): State? {
        return storage.getState()
    }

    protected abstract fun handleIntent(intent: Intent): State
}
