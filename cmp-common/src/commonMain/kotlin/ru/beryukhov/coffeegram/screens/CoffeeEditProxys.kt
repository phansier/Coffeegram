@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import kotlinx.datetime.LocalDate
import ru.beryukhov.coffeegram.app_ui.handleKeyDown
import ru.beryukhov.coffeegram.components.CoffeeEditComponent
import ru.beryukhov.coffeegram.components.CoffeeEditComponent.DetailsConfig
import ru.beryukhov.coffeegram.components.DayListComponent
import ru.beryukhov.coffeegram.components.MonthTableComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CoffeeEditAppBar(
    coffeeEditComponent: CoffeeEditComponent,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val state by coffeeEditComponent.panels.subscribeAsState()
    val details = state.details
    if (!LocalWindowLayout.current.isWide && details != null) {
        DayListAppBar(details.instance, scrollBehavior = scrollBehavior)
    } else {
        val selectedDay = (details?.configuration as? DetailsConfig.DayList)?.date
        MonthTableAppBar(state.main.instance, selectedDay = selectedDay, scrollBehavior = scrollBehavior)
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CoffeeEditScreen(
    coffeeEditComponent: CoffeeEditComponent,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val isWide = LocalWindowLayout.current.isWide
    val targetMode = if (isWide) ChildPanelsMode.DUAL else ChildPanelsMode.SINGLE
    LaunchedEffect(targetMode) { coffeeEditComponent.setMode(targetMode) }

    val state by coffeeEditComponent.panels.subscribeAsState()
    val details = state.details
    val selectedDay = (details?.configuration as? DetailsConfig.DayList)?.date

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { event ->
                details != null && event.handleKeyDown(Key.Escape, action = coffeeEditComponent::onBack)
            },
    ) {
        if (isWide) {
            DualPaneCoffeeEdit(
                monthTableComponent = state.main.instance,
                dayListComponent = details?.instance,
                selectedDay = selectedDay,
                contentPadding = contentPadding,
            )
        } else {
            if (details != null) {
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

@Composable
private fun DualPaneCoffeeEdit(
    monthTableComponent: MonthTableComponent,
    dayListComponent: DayListComponent?,
    selectedDay: LocalDate?,
    contentPadding: PaddingValues,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val detailPaneModifier = Modifier.width((maxWidth / 2).coerceAtMost(MaxDetailPaneWidth))
        Row(modifier = Modifier.fillMaxSize()) {
            MonthTableScreen(
                component = monthTableComponent,
                selectedDay = selectedDay,
                modifier = Modifier.weight(1f).padding(contentPadding),
            )
            if (dayListComponent != null) {
                DayListScreen(
                    component = dayListComponent,
                    contentPadding = contentPadding,
                    modifier = detailPaneModifier,
                )
            } else {
                DayListPlaceholder(
                    contentPadding = contentPadding,
                    modifier = detailPaneModifier,
                )
            }
        }
    }
}

private val MaxDetailPaneWidth = 400.dp
