package ru.beryukhov.coffeegram.wear

import android.content.Context
import android.content.Intent
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.KEY
import ru.beryukhov.coffeegram.data.START_ACTIVITY_PATH
import ru.beryukhov.coffeegram.data.toDayCoffee

interface MessageHandler {
    fun onMessageReceived(messageEvent: MessageEvent)
}

class MessageHandlerImpl(private val context: Context) : MessageHandler {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            START_ACTIVITY_PATH -> context.startActivity(
                Intent(context, WearActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                null
            )
        }
    }
}

interface DataHandler {
    fun onDataReceived(dataEvent: DataEvent)
}

class DataHandlerImpl : DataHandler {
    override fun onDataReceived(dataEvent: DataEvent) {
        val uri = dataEvent.dataItem.uri
        when (uri.path) {
            DAY_COFFEE_PATH ->
                DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.getIntegerArrayList(KEY)?.toDayCoffee()?.let {
                    coffeeState.value = it
                }
        }
    }
}

class DataLayerListenerService : WearableListenerService() {
    private val messageHandler: MessageHandler by lazy { MessageHandlerImpl(this) }
    private val dataHandler: DataHandler by lazy { DataHandlerImpl() }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        messageHandler.onMessageReceived(messageEvent)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { dataEvent ->
            dataHandler.onDataReceived(dataEvent)
        }
    }
}
