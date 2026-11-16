package ru.beryukhov.coffeegram.wear

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material3.AppScaffold
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DB_KEY
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DELTA
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.currentDate
import ru.beryukhov.coffeegram.data.currentDateFlow
import ru.beryukhov.coffeegram.data.dayCoffeeOn

private const val TAG = "WearActivity"

class WearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        primeCoffeeStateFromDataLayer()
        setContent {
            CoffeegramTheme {
                AppScaffold {
                    TodayRoute()
                }
            }
        }
    }

    private fun primeCoffeeStateFromDataLayer() {
        val dataClient = Wearable.getDataClient(this)
        val uri = Uri.Builder()
            .scheme(PutDataRequest.WEAR_URI_SCHEME)
            .path(DAY_COFFEE_PATH)
            .build()
        lifecycleScope.launch {
            try {
                dataClient.getDataItems(uri).await().use { buffer ->
                    buffer.firstOrNull()?.let { item ->
                        acceptPhoneSnapshot(DataMapItem.fromDataItem(item).dataMap)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                Log.d(TAG, "Priming coffee state failed: $exception")
            }
        }
    }
}

@Composable
private fun TodayRoute() {
    val datedDayCoffee by coffeeState.collectAsState()
    val today by remember { currentDateFlow() }.collectAsState(initial = currentDate())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    TodayScreen(
        dayCoffee = datedDayCoffee.dayCoffeeOn(today),
        onIncrement = { type -> changeCoffeeCount(type, +1) { scope.launch { sendCoffeeChange(context, type, +1) } } },
        onDecrement = { type -> changeCoffeeCount(type, -1) { scope.launch { sendCoffeeChange(context, type, -1) } } },
    )
}

private fun changeCoffeeCount(type: CoffeeType, delta: Int, onChanged: () -> Unit) {
    if (applyDelta(type, delta)) onChanged()
}

private suspend fun sendCoffeeChange(context: Context, type: CoffeeType, delta: Int) {
    val uniquePath = COFFEE_EVENT_PATH_PREFIX + java.util.UUID.randomUUID().toString()
    val request = PutDataMapRequest.create(uniquePath).apply {
        dataMap.putString(COFFEE_EVENT_KEY_DB_KEY, type.dbKey)
        dataMap.putInt(COFFEE_EVENT_KEY_DELTA, delta)
    }.asPutDataRequest().setUrgent()
    try {
        Wearable.getDataClient(context).putDataItem(request).await()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Log.d(TAG, "putDataItem event failed: $exception")
    }
}
