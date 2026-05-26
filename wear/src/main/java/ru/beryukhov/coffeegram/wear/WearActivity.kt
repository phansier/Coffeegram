package ru.beryukhov.coffeegram.wear

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

@Preview
@Composable
internal fun PagesContent() {
    val dayCoffee by coffeeState.collectAsState()
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
                CoffeeItem(c = pair.coffee, count = pair.count)
            }
        )
    }
}

@Composable
fun CoffeeItem(c: CoffeeType, count: Int, modifier: Modifier = Modifier) {
    Chip(
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
