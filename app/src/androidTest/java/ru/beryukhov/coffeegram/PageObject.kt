package ru.beryukhov.coffeegram

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithLabel
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.TablePage

abstract class PageObject<out T : PageObject<T>> {
    abstract val Page: @Composable () -> Unit
}
typealias PageElement = ComposeTestRule.() -> SemanticsNodeInteraction

object TablePageObject : PageObject<TablePageObject>() {
    override val Page: @Composable () -> Unit = { TablePage(TODO(), TODO(), TODO()) }
    val ComposeTestRule.LeftArrowButton get() = onNodeWithLabel("ArrowLeft")
    val ComposeTestRule.RightArrowButton get() = onNodeWithTag("ArrowRight")
    //todo more complex logic to determine widgets than by it's text
    fun ComposeTestRule.Month(name: String) = onNodeWithText(name)
    fun ComposeTestRule.Day(number: String) = onNodeWithText(number)
}

object CoffeeListPageObject : PageObject<CoffeeListPageObject>() {
    override val Page: @Composable () -> Unit = { CoffeeListPage(TODO(), TODO()) }
    //todo more complex logic to determine widgets than by it's text
    val ComposeTestRule.CappuccinoItem get() = onNodeWithText("Cappuccino")
    val ComposeTestRule.LatteItem get() = onNodeWithText("Latte")
}
