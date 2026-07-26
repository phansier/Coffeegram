package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.view.CoffeeShopList
import ru.beryukhov.coffeegram.view.CoffeeShopListStyle

/**
 * Specialty tab: on wide screens the map and the coffee-shop list sit side by side; on narrow
 * screens the list lives in a draggable bottom sheet over the map instead.
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
                VerticalDivider()
                SidePane(modifier = Modifier.fillMaxHeight()) {
                    CoffeeShopList(
                        component = component,
                        modifier = Modifier.fillMaxSize(),
                        style = CoffeeShopListStyle.Card,
                    )
                }
            }
        } else {
            BottomSheetPane(
                modifier = Modifier.fillMaxSize(),
                sheetContent = {
                    CoffeeShopList(
                        component = component,
                        modifier = Modifier.fillMaxWidth(),
                        style = CoffeeShopListStyle.Flat,
                    )
                },
            ) {
                MapScreen(
                    component = component,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
