package ru.beryukhov.coffeegram.wearable

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.currentDate
import ru.beryukhov.coffeegram.data.toCoffeeChangeEvent
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

private const val TAG = "PhoneDataLayerSvc"

class PhoneDataLayerListenerService : WearableListenerService(), KoinComponent {
    private val daysCoffeesStore: DaysCoffeesStore by inject()
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val wearableSyncService by lazy { WearableSyncService(this) }

    override fun onDataChanged(events: DataEventBuffer) {
        super.onDataChanged(events)
        events.forEach { event ->
            val path = event.dataItem.uri.path.orEmpty()
            if (!path.startsWith(COFFEE_EVENT_PATH_PREFIX)) return@forEach
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            handleEvent(event)
        }
    }

    private fun handleEvent(event: DataEvent) {
        val today = currentDate()
        val change = DataMapItem.fromDataItem(event.dataItem).dataMap.toCoffeeChangeEvent(dateWhenMissing = today)
        if (change == null) {
            Log.d(TAG, "Discarding event ${event.dataItem.uri}")
        } else {
            applyAndPublishBeforeConfirming(change.toDaysCoffeesIntent(), today)
        }
        try {
            // Block the binder thread until deletion completes so the service
            // can't be killed mid-flight and replay the event later.
            Tasks.await(dataClient.deleteDataItems(event.dataItem.uri))
        } catch (exception: Exception) {
            Log.d(TAG, "deleteDataItems failed for ${event.dataItem.uri}: $exception")
        }
    }

    private fun applyAndPublishBeforeConfirming(intent: DaysCoffeesIntent, today: LocalDate) = runBlocking {
        val applied = daysCoffeesStore.applyIntent(intent)
        wearableSyncService.putDayCoffee(DatedDayCoffee(date = today, dayCoffee = applied.coffees[today] ?: DayCoffee()))
    }
}
