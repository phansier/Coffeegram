package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class DayListScreen(private val provider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DayListScreen>(provider, { hasTestTag("DayList") }) {

    fun coffee(name: String) = KNode(provider) {
        useUnmergedTree = true
        hasTestTag("CoffeeName")
        hasText(name)
    }

    fun coffeeItem(name: String) = CoffeeItem(provider, name)
}
