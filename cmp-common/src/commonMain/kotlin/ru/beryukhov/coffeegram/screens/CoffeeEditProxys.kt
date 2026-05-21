package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import ru.beryukhov.coffeegram.components.CoffeeEditComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CoffeeEditAppBar(coffeeEditComponent: CoffeeEditComponent) {
    val state by coffeeEditComponent.panels.subscribeAsState()
    val details = state.details
    if (state.mode == ChildPanelsMode.SINGLE && details != null) {
        DayListAppBar(details.instance)
    } else {
        MonthTableAppBar(state.main.instance)
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CoffeeEditScreen(
    coffeeEditComponent: CoffeeEditComponent,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val mode = if (maxWidth >= WIDE_SCREEN_THRESHOLD) ChildPanelsMode.DUAL else ChildPanelsMode.SINGLE
        LaunchedEffect(mode) { coffeeEditComponent.setMode(mode) }

        val state by coffeeEditComponent.panels.subscribeAsState()
        val details = state.details

        when (state.mode) {
            ChildPanelsMode.DUAL -> Row(modifier = Modifier.fillMaxSize()) {
                MonthTableScreen(
                    component = state.main.instance,
                    modifier = Modifier.weight(1f).padding(contentPadding),
                )
                if (details != null) {
                    DayListScreen(
                        component = details.instance,
                        contentPadding = contentPadding,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            else -> if (details != null) {
                DayListScreen(
                    component = details.instance,
                    contentPadding = contentPadding,
                )
            } else {
                MonthTableScreen(
                    component = state.main.instance,
                    modifier = Modifier.padding(contentPadding),
                )
            }
        }
    }
}

private val WIDE_SCREEN_THRESHOLD = 600.dp
