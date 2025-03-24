package ru.beryukhov.coffeegram.screens

import androidx.compose.runtime.Composable
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
fun CoffeeEditScreen(coffeeEditComponent: CoffeeEditComponent) {
    Children(
        stack = coffeeEditComponent.childStack,
    ) { child ->
        when (val c = child.instance) {
            is CoffeeEditComponent.Child.MonthTable -> MonthTableScreen(c.component)
            is CoffeeEditComponent.Child.DayList -> DayListScreen(c.component)
        }
    }
}
