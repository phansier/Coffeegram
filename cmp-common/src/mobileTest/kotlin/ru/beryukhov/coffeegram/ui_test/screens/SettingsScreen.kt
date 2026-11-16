package ru.beryukhov.coffeegram.ui_test.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.KNode

class SettingsScreen(provider: SemanticsNodeInteractionsProvider) {
    val styleSection = KNode(provider) { hasText("Style") }
}
