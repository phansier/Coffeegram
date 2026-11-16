package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TableScreen(private val provider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TableScreen>(provider) {

    val month = KNode(provider) { hasTestTag("Month") }
    val previousMonthButton = KNode(provider) { hasContentDescription("ArrowLeft") }
    val nextMonthButton = KNode(provider) { hasTestTag("ArrowRight") }

    fun day(number: String) = KNode(provider) {
        hasTestTag("Day")
        hasText(number)
    }

    fun dayAt(index: Int): SemanticsNodeInteraction = provider.onAllNodesWithTag("Day")[index]

    fun monthTitle(): String {
        val month = provider.onNodeWithTag("Month", useUnmergedTree = true).fetchSemanticsNode()
        return (listOf(month) + month.children)
            .flatMap { node -> node.config.getOrNull(SemanticsProperties.Text).orEmpty() }
            .joinToString(separator = " ") { it.text }
    }
}
