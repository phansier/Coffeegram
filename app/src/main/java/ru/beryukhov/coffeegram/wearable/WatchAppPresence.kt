package ru.beryukhov.coffeegram.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import ru.beryukhov.coffeegram.data.WEAR_CAPABILITY

private const val TAG = "WatchAppPresence"

fun watchAppInstalledFlow(context: Context): Flow<Boolean> = callbackFlow {
    val capabilityClient = Wearable.getCapabilityClient(context)
    val listener = CapabilityClient.OnCapabilityChangedListener { info ->
        trySend(info.nodes.isNotEmpty())
    }
    capabilityClient.addListener(listener, WEAR_CAPABILITY)
    send(capabilityClient.hasWatchApp())
    awaitClose { capabilityClient.removeListener(listener, WEAR_CAPABILITY) }
}.distinctUntilChanged()

private suspend fun CapabilityClient.hasWatchApp(): Boolean = try {
    getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_ALL).await().nodes.isNotEmpty()
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (exception: Exception) {
    Log.d(TAG, "capability lookup failed: $exception")
    false
}
