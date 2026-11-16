package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import androidx.compose.ui.test.hasText as textMatcher

class CoffeeItem(provider: SemanticsNodeInteractionsProvider, name: String) :
    ComposeScreen<CoffeeItem>(
        provider,
        {
            hasTestTag("CoffeeItem")
            hasAnyDescendant(textMatcher(name))
        }
    ) {

    val decrementButton: KNode = child { hasText("-") }
    val incrementButton: KNode = child { hasText("+") }

    fun assertCount(count: Int) = assertValueEquals(count.toString())
}
