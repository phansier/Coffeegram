package ru.beryukhov.coffeegram.wear

import android.net.Uri
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.CoffeeChangeEvent
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.currentDate
import ru.beryukhov.coffeegram.data.currentDateFlow
import ru.beryukhov.coffeegram.data.toCoffeeChangeEvent
import ru.beryukhov.coffeegram.data.toDatedDayCoffee
import ru.beryukhov.coffeegram.data.toPutDataRequest
import java.util.UUID

private const val TAG = "WearCoffeeRepository"

class WearCoffeeRepository(private val dataClient: DataClient) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val state = MutableStateFlow(WearCoffeeState(DatedDayCoffee(date = currentDate())))

    val todayCoffee: Flow<DayCoffee> =
        combine(state, currentDateFlow()) { current, today -> current.dayCoffeeOn(today) }

    fun loadFromDataLayer() {
        scope.launch {
            try {
                val today = currentDate()
                val phoneSnapshots = async { dataClient.readDataMaps(DAY_COFFEE_PATH, DataClient.FILTER_LITERAL) }
                val outbox = async { dataClient.readDataMaps(COFFEE_EVENT_PATH_PREFIX, DataClient.FILTER_PREFIX) }
                phoneSnapshots.await().values.firstOrNull()
                    ?.toDatedDayCoffee(dateWhenMissing = today)
                    ?.let { snapshot -> state.update { it.withPhoneSnapshot(snapshot) } }
                val unconfirmed = outbox.await()
                    .mapNotNull { (path, map) -> map.toCoffeeChangeEvent(dateWhenMissing = today)?.let { path to it } }
                    .toMap()
                state.update { it.withUnconfirmedChanges(unconfirmed) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                Log.d(TAG, "Loading from data layer failed: $exception")
            }
        }
    }

    fun onPhoneSnapshot(dataMap: DataMap) {
        val snapshot = dataMap.toDatedDayCoffee(dateWhenMissing = currentDate()) ?: return
        state.update { it.withPhoneSnapshot(snapshot) }
    }

    fun onChangeConfirmed(eventPath: String) {
        state.update { it.withConfirmedChange(eventPath) }
    }

    fun change(type: CoffeeType, delta: Int) {
        val today = currentDate()
        val currentCount = state.value.dayCoffeeOn(today).coffeeCountMap[type] ?: 0
        if (currentCount + delta < 0) return
        val eventPath = COFFEE_EVENT_PATH_PREFIX + UUID.randomUUID()
        val change = CoffeeChangeEvent(date = today, type = type, delta = delta)
        state.update { it.withLocalChange(eventPath, change) }
        scope.launch { send(eventPath, change) }
    }

    private suspend fun send(eventPath: String, change: CoffeeChangeEvent) {
        try {
            dataClient.putDataItem(change.toPutDataRequest(eventPath)).await()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            Log.d(TAG, "Sending $eventPath failed: $exception")
            state.update { it.withoutLocalChange(eventPath) }
        }
    }
}

private suspend fun DataClient.readDataMaps(path: String, filter: Int): Map<String, DataMap> {
    val uri = Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build()
    return getDataItems(uri, filter).await().use { buffer ->
        buffer.associate { item -> item.uri.path.orEmpty() to DataMapItem.fromDataItem(item).dataMap }
    }
}
