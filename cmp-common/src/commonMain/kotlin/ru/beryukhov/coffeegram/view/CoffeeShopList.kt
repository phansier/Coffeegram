package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.frappe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import ru.beryukhov.coffeegram.app_ui.PreviewTheme
import ru.beryukhov.coffeegram.components.CoffeeShopsState
import ru.beryukhov.coffeegram.components.ExtendedCoffeeShop
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.repository.CoffeeShop

@Composable
fun CoffeeShopList(
    component: MapComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.coffeeShops.collectAsState()
    val listState = rememberLazyListState()

    // When a shop is selected from a map marker, bring it into view. Only scroll when it's
    // off-screen, so selecting an already-visible row (or a list tap) doesn't jump the list.
    val highlightedIndex = state.list.indexOfFirst { it.highlighted }
    LaunchedEffect(highlightedIndex) {
        if (highlightedIndex >= 0 &&
            listState.layoutInfo.visibleItemsInfo.none { it.index == highlightedIndex }
        ) {
            listState.animateScrollToItem(highlightedIndex)
        }
    }

    LazyColumn(state = listState, modifier = modifier) {
        // Key on index: the remote data can contain duplicate name/coordinates, and the list
        // order is stable (only `highlighted` flags toggle, preserving order).
        itemsIndexed(state.list) { _, shop ->
            CoffeeShopListItem(
                shop = shop,
                onClick = { component.onMarkerClicked(shop.coffeeShop) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun CoffeeShopListItem(
    shop: ExtendedCoffeeShop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (shop.highlighted) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.frappe),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = shop.coffeeShop.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = shop.coffeeShop.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CoffeeShopListPreview() = PreviewTheme {
    CoffeeShopList(
        component = object : MapComponent {
            override val coffeeShops: StateFlow<CoffeeShopsState> = MutableStateFlow(
                CoffeeShopsState(
                    list = listOf(
                        ExtendedCoffeeShop(CoffeeShop("Coltivare", LoremIpsum(40).values.joinToString(), 0.0, 0.0), highlighted = true),
                        ExtendedCoffeeShop(CoffeeShop("Stooker Roastery", "Pour over · 420 m", 0.0, 0.0)),
                        ExtendedCoffeeShop(CoffeeShop("Black Sheep", "Filter · 900 m", 0.0, 0.0)),
                    ),
                    isLoading = false,
                )
            )

            override fun onZoomChanged(zoom: Float) = Unit
            override fun onMarkerClicked(coffeeShop: CoffeeShop) = Unit
        },
    )
}
