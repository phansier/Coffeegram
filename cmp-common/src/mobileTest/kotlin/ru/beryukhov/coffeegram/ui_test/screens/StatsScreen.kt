package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.KNode

class StatsScreen(provider: SemanticsNodeInteractionsProvider) {
    val title = KNode(provider) { hasText("Statistics", ignoreCase = true) }
}
