package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.KNode

class NavigationBar(private val provider: SemanticsNodeInteractionsProvider) {

    val calendar = tab("Calendar")
    val stats = tab("Stats")
    val settings = tab("Settings")

    private fun tab(label: String) = KNode(provider) {
        hasText(label)
        isSelectable()
    }
}
