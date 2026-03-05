package ru.beryukhov.coffeegram.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.START_ACTIVITY_PATH
import ru.beryukhov.coffeegram.data.toDataMap

private const val TAG = "WearableSyncService"

/**
 * Service for syncing data with wearable devices.
 */
class WearableSyncService(
    private val context: Context,
) {
    private val nodeClient by lazy { Wearable.getNodeClient(context) }
    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val dataClient by lazy { Wearable.getDataClient(context) }

    /**
     * Start the wearable activity on connected devices.
     */
    fun startWearableActivity(scope: CoroutineScope, dayCoffee: DayCoffee? = null) {
        scope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()

                // Send a message to all nodes in parallel
                nodes.map { node ->
                    async {
                        messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf()).await()
                    }
                }.awaitAll()

                Log.d(TAG, "Starting activity requests sent successfully")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Starting activity failed: $exception")
            }
        }

        // Also send current day coffee data if provided
        dayCoffee?.let { sendDayCoffee(scope, it) }
    }

    /**
     * Send the current day coffee data to wearable devices.
     */
    fun sendDayCoffee(scope: CoroutineScope, dayCoffee: DayCoffee) {
        scope.launch {
            try {
                val request = PutDataMapRequest.create(DAY_COFFEE_PATH).apply {
                    dayCoffee.toDataMap(dataMap)
                }.asPutDataRequest().setUrgent()

                val result = dataClient.putDataItem(request).await()

                Log.d(TAG, "DataItem saved: $result")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Saving DataItem failed: $exception")
            }
        }
    }
}
