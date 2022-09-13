package ru.beryukhov.coffeegram.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.itemsIndexed
import kotlinx.coroutines.flow.MutableStateFlow
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee

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
fun PagesContent() {
    val dayCoffee by coffeeState.collectAsState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(
            top = 28.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 40.dp
        ),
    ) {
        itemsIndexed(items = dayCoffee.coffeeCountMap.withEmpty(),
            itemContent = { _, pair: Pair<CoffeeType, Int> ->
                CoffeeItem(c = pair.first, count = pair.second)
            }
        )
    }
}

@Composable
fun CoffeeItem(c: CoffeeType, count: Int, modifier: Modifier = Modifier) {
    Chip(
        icon = {
            Image(
                painter = painterResource(id = c.iconId),
                contentDescription = "",

                modifier = Modifier
                    .size(ChipDefaults.IconSize)
                    .alpha(LocalContentAlpha.current)
            )
        },
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = c.nameId) + " - " + count
            )
        },
        onClick = {},
    )
}

// todo share it
@VisibleForTesting
internal fun Map<CoffeeType, Int>.withEmpty(): List<Pair<CoffeeType, Int>> {
    data class MutablePair(val ct: CoffeeType, var count: Int)

    val emptyList: MutableList<MutablePair> =
        CoffeeType.values().toList().map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter { it.ct == entry.key }.forEach { it.count = entry.value }
    }
    return emptyList.map { it.ct to it.count }
}
