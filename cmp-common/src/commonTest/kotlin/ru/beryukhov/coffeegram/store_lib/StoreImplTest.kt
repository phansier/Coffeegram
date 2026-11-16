package ru.beryukhov.coffeegram.store_lib

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StoreImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private class FakeStorage(storedState: List<Int>) : Storage<List<Int>> {
        val loadAllowed = CompletableDeferred<Unit>()
        private val stored = storedState
        val saved = mutableListOf<List<Int>>()

        override suspend fun getState(): List<Int> {
            loadAllowed.await()
            return stored
        }

        override suspend fun saveState(state: List<Int>) {
            saved += state
        }
    }

    private class AppendingStore(storage: Storage<List<Int>>) : StoreImpl<Int, List<Int>>(emptyList(), storage) {
        override fun List<Int>.handleIntent(intent: Int): List<Int> = this + intent
    }

    @Test
    fun intentSentBeforeStoredStateLoadsIsAppliedOnTopOfIt() = runTest(testDispatcher) {
        val storage = FakeStorage(storedState = listOf(10))
        val store = AppendingStore(storage)

        store.newIntent(1)
        storage.loadAllowed.complete(Unit)
        val finalState = store.applyIntent(2)

        assertEquals(listOf(10, 1, 2), finalState)
        assertEquals(listOf(10, 1, 2), store.state.value)
        assertEquals(listOf(10, 1, 2), storage.saved.last())
    }

    @Test
    fun applyIntentReturnsStateIncludingThatIntent() = runTest(testDispatcher) {
        val storage = FakeStorage(storedState = listOf(10)).apply { loadAllowed.complete(Unit) }
        val store = AppendingStore(storage)

        assertEquals(listOf(10, 5), store.applyIntent(5))
    }

    @Test
    fun intentsAreAppliedOneAtATimeInOrder() = runTest(testDispatcher) {
        val storage = FakeStorage(storedState = emptyList()).apply { loadAllowed.complete(Unit) }
        val store = AppendingStore(storage)

        (1..3).forEach(store::newIntent)
        val applied = async(start = CoroutineStart.UNDISPATCHED) { store.applyIntent(4) }
        (5..6).forEach(store::newIntent)
        val finalState = store.applyIntent(7)

        assertEquals(listOf(1, 2, 3, 4), applied.await())
        assertEquals((1..7).toList(), finalState)
        assertEquals(7, storage.saved.size)
    }
}
