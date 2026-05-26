package ru.beryukhov.coffeegram.wearable

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DB_KEY
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DELTA
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import kotlin.time.Clock

private const val TAG = "PhoneDataLayerSvc"

class PhoneDataLayerListenerService : WearableListenerService(), KoinComponent {
    private val daysCoffeesStore: DaysCoffeesStore by inject()
    private val dataClient by lazy { Wearable.getDataClient(this) }

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
        val map = DataMapItem.fromDataItem(event.dataItem).dataMap
        val dbKey = map.getString(COFFEE_EVENT_KEY_DB_KEY)
        val delta = map.getInt(COFFEE_EVENT_KEY_DELTA, 0)
        val type = CoffeeTypes.entries.find { it.dbKey == dbKey }
        if (type == null || delta == 0) {
            Log.d(TAG, "Discarding event dbKey=$dbKey delta=$delta")
        } else {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val intent = if (delta > 0) {
                DaysCoffeesIntent.PlusCoffee(today, type)
            } else {
                DaysCoffeesIntent.MinusCoffee(today, type)
            }
            daysCoffeesStore.newIntent(intent)
        }
        try {
            // Block the binder thread until deletion completes so the service
            // can't be killed mid-flight and replay the event later.
            Tasks.await(dataClient.deleteDataItems(event.dataItem.uri))
        } catch (exception: Exception) {
            Log.d(TAG, "deleteDataItems failed for ${event.dataItem.uri}: $exception")
        }
    }
}
