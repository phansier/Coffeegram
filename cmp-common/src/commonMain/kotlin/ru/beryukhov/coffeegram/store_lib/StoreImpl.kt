package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This store implementation can work as InMemoryStore without provided storage
 * or as PersistentStore with provided storage.
 */
abstract class StoreImpl<Intent : Any, State : Any>(
    initialState: State,
    private val storage: Storage<State>? = null
) : Store<Intent, State> {
    private val intentFlow = MutableSharedFlow<Intent>()
    private val stateFlow = MutableStateFlow(initialState)

    override val state: StateFlow<State>
        get() = stateFlow

    init {
        MainScope().launch {
            withContext(Dispatchers.Default) {
                getStoredState()?.let {
                    stateFlow.value = it
                }
            }
            handleIntents()
        }
    }

    override fun newIntent(intent: Intent) {
        MainScope().launch {
            intentFlow.emit(intent)
        }
    }

    private suspend fun handleIntents() {
        intentFlow.collect {
            stateFlow.value = stateFlow.value.handleIntent(intent = it)
            withContext(Dispatchers.Default) {
                storage?.saveState(stateFlow.value)
            }
        }
    }

    private suspend fun getStoredState(): State? {
        return storage?.getState()
    }

    protected abstract fun State.handleIntent(intent: Intent): State
}
