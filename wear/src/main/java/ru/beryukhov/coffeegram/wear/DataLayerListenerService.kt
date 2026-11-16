package ru.beryukhov.coffeegram.wear

import android.content.Intent
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.START_ACTIVITY_PATH

class DataLayerListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        if (messageEvent.path == START_ACTIVITY_PATH) {
            startActivity(Intent(this, WearActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.forEach(::onDataEvent)
    }

    private fun onDataEvent(dataEvent: DataEvent) {
        val path = dataEvent.dataItem.uri.path.orEmpty()
        when {
            path == DAY_COFFEE_PATH && dataEvent.type == DataEvent.TYPE_CHANGED ->
                coffeeRepository.onPhoneSnapshot(DataMapItem.fromDataItem(dataEvent.dataItem).dataMap)
            path.startsWith(COFFEE_EVENT_PATH_PREFIX) && dataEvent.type == DataEvent.TYPE_DELETED ->
                coffeeRepository.onChangeConfirmed(path)
        }
    }
}
