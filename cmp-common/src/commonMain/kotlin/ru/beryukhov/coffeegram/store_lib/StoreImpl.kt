package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * This store implementation can work as InMemoryStore without provided storage
 * or as PersistentStore with provided storage.
 */
abstract class StoreImpl<Intent : Any, State : Any>(
    initialState: State,
    private val storage: Storage<State>? = null
) : Store<Intent, State> {
    private val scope = MainScope()
    private val stateFlow = MutableStateFlow(initialState)
    private val storedStateLoaded = CompletableDeferred<Unit>()
    private val intentLock = Mutex()

    override val state: StateFlow<State>
        get() = stateFlow

    init {
        scope.launch {
            withContext(Dispatchers.Default) {
                getStoredState()?.let {
                    stateFlow.value = it
                }
            }
            storedStateLoaded.complete(Unit)
        }
    }

    override fun newIntent(intent: Intent) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) { applyIntent(intent) }
    }

    override suspend fun applyIntent(intent: Intent): State =
        intentLock.withLock {
            storedStateLoaded.await()
            val newState = stateFlow.value.handleIntent(intent = intent)
            stateFlow.value = newState
            withContext(Dispatchers.Default) {
                storage?.saveState(newState)
            }
            newState
        }

    private suspend fun getStoredState(): State? {
        return storage?.getState()
    }

    protected abstract fun State.handleIntent(intent: Intent): State
}
