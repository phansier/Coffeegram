package ru.beryukhov.coffeegram

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TableScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TableScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag("TableScreen")
        }
    ) {
    val leftArrowButton = KNode(semanticsProvider) {
        hasContentDescription("LeftArrow")
    }
    val rightArrowButton = KNode(semanticsProvider) {
        hasTestTag("RightArrow")
    }
    val monthName = KNode(semanticsProvider) {
        hasTestTag("Month")
    }
    val yearName = KNode(semanticsProvider) {
        hasTestTag("Year")
    }
    fun day(number: String) = onNode {
        hasTestTag("Day")
        hasText(number)
    }
}
