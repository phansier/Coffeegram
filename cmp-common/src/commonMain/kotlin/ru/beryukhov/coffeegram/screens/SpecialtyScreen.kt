package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.view.CoffeeShopList

/**
 * Specialty tab: on wide screens the map and the coffee-shop list sit side by side, on narrow
 * screens only the map is shown (the list collapses, matching the phone layout).
 */
@Composable
fun SpecialtyScreen(
    component: MapComponent,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth >= WIDE_SCREEN_THRESHOLD) {
            Row(modifier = Modifier.fillMaxSize()) {
                MapScreen(
                    component = component,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    showMarkerDescription = false,
                )
                CoffeeShopList(
                    component = component,
                    modifier = Modifier.width(320.dp).fillMaxHeight(),
                )
            }
        } else {
            MapScreen(
                component = component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

