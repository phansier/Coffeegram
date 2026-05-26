package ru.beryukhov.coffeegram.wear

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DB_KEY
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_KEY_DELTA
import ru.beryukhov.coffeegram.data.COFFEE_EVENT_PATH_PREFIX
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DAY_COFFEE_PATH
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.KEY
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.data.toDayCoffee
import ru.beryukhov.coffeegram.data.withEmpty

private const val TAG = "WearActivity"

val coffeeState: MutableStateFlow<DayCoffee> by lazy { MutableStateFlow(value = DayCoffee()) }

class WearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        primeCoffeeStateFromDataLayer()
        setContent {
            CoffeegramTheme {
                PagesContent()
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
                        DataMapItem.fromDataItem(item).dataMap.getIntegerArrayList(KEY)
                            ?.toDayCoffee()
                            ?.let { coffeeState.value = it }
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

private const val SWIPE_STEP_DP = 56

@Preview
@Composable
internal fun PagesContent() {
    val dayCoffee by coffeeState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 28.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 40.dp
        ),
    ) {
        itemsIndexed(
            items = dayCoffee.coffeeCountMap.withEmpty(),
            itemContent = { _, pair: CoffeeTypeWithCount ->
                CoffeeItem(
                    c = pair.coffee,
                    count = pair.count,
                    onIncrement = {
                        if (applyDelta(pair.coffee, +1)) {
                            scope.launch { sendCoffeeChange(context, pair.coffee, +1) }
                        }
                    },
                    onDecrement = {
                        if (applyDelta(pair.coffee, -1)) {
                            scope.launch { sendCoffeeChange(context, pair.coffee, -1) }
                        }
                    },
                )
            }
        )
    }
}

private fun applyDelta(type: CoffeeType, delta: Int): Boolean {
    var changed = false
    coffeeState.update { day ->
        val current = day.coffeeCountMap[type] ?: 0
        val next = (current + delta).coerceAtLeast(0)
        if (next == current) {
            day
        } else {
            changed = true
            DayCoffee(day.coffeeCountMap + (type to next))
        }
    }
    return changed
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

@Composable
fun CoffeeItem(
    c: CoffeeType,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Chip(
        modifier = modifier.pointerInput(c) {
            val threshold = SWIPE_STEP_DP.dp.toPx()
            var dragAccum = 0f
            detectHorizontalDragGestures(
                onDragEnd = { dragAccum = 0f },
                onDragCancel = { dragAccum = 0f },
            ) { _, dx ->
                dragAccum += dx
                while (dragAccum >= threshold) {
                    onIncrement()
                    dragAccum -= threshold
                }
                while (dragAccum <= -threshold) {
                    onDecrement()
                    dragAccum += threshold
                }
            }
        },
        icon = {
            c.icon(
                modifier = Modifier
                    .size(ChipDefaults.IconSize)
                    .alpha(LocalContentAlpha.current)
            )
        },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = printableText(c.localizedName) + " - " + count
            )
        },
        onClick = {},
    )
}

