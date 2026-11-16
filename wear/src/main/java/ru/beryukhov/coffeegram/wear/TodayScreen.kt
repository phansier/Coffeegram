package ru.beryukhov.coffeegram.wear

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.withEmpty

@Composable
fun TodayScreen(
    dayCoffee: DayCoffee,
    onIncrement: (CoffeeType) -> Unit,
    onDecrement: (CoffeeType) -> Unit,
) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()
    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(items = dayCoffee.coffeeCountMap.withEmpty(), key = { it.coffee.dbKey }) { coffeeWithCount ->
                CoffeeItem(
                    c = coffeeWithCount.coffee,
                    count = coffeeWithCount.count,
                    isListScrolling = listState.isScrollInProgress,
                    transformation = SurfaceTransformation(transformationSpec),
                    onIncrement = { onIncrement(coffeeWithCount.coffee) },
                    onDecrement = { onDecrement(coffeeWithCount.coffee) },
                    modifier = Modifier
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
private fun TodayScreenPreview() {
    CoffeegramTheme {
        AppScaffold {
            TodayScreen(
                dayCoffee = DayCoffee(mapOf(CoffeeTypes.Cappuccino to 2, CoffeeTypes.Americano to 1)),
                onIncrement = {},
                onDecrement = {},
            )
        }
    }
}
