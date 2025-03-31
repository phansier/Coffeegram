package ru.beryukhov.coffeegram.wear

import android.os.Bundle
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.Text
import kotlinx.coroutines.flow.MutableStateFlow
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypeWithCount
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.printableText
import ru.beryukhov.coffeegram.data.withEmpty

val coffeeState: MutableStateFlow<DayCoffee> by lazy { MutableStateFlow(value = DayCoffee()) }

class WearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeegramTheme {
                PagesContent()
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
