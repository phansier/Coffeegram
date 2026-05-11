package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.beryukhov.coffeegram.components.CoffeeEditComponent

@Composable
fun CoffeeEditAppBar(coffeeEditComponent: CoffeeEditComponent) {
    Children(
        stack = coffeeEditComponent.childStack,
    ) { child ->
        when (val c = child.instance) {
            is CoffeeEditComponent.Child.MonthTable -> MonthTableAppBar(c.component)
            is CoffeeEditComponent.Child.DayList -> DayListAppBar(c.component)
        }
    }
}

@Composable
fun CoffeeEditScreen(
    coffeeEditComponent: CoffeeEditComponent,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Children(
        stack = coffeeEditComponent.childStack,
    ) { child ->
        when (val c = child.instance) {
            is CoffeeEditComponent.Child.MonthTable -> MonthTableScreen(
                component = c.component,
                modifier = Modifier.padding(contentPadding),
            )
            is CoffeeEditComponent.Child.DayList -> DayListScreen(
                component = c.component,
                contentPadding = contentPadding,
            )
        }
    }
}
