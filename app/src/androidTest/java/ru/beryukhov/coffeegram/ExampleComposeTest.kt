package ru.beryukhov.coffeegram

import androidx.ui.test.*
import org.junit.Rule
import org.junit.Test
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore


class ExampleComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Test
    fun testYear() {
        withRule {
            onNodeWithText("2020").assertIsDisplayed()
        }
    }

    @Test
    fun testMonthChange() {
        withRule {
            onNodeWithText("September").assertIsDisplayed()

            onNodeWithLabel("ArrowLeft").assertIsDisplayed().performClick()
            onNodeWithText("August").assertIsDisplayed()
            onNodeWithLabel("ArrowRight").assertIsDisplayed().performClick()
            onNodeWithText("September").assertIsDisplayed()
            onNodeWithLabel("ArrowRight").assertIsDisplayed().performClick()
            onNodeWithText("October").assertIsDisplayed()

        }
    }

    private inline fun <R> withRule(block: ComposeTestRuleJUnit.() -> R): R = with(composeTestRule) {
        setContent {
            PagesContent(NavigationStore(), DaysCoffeesStore())
        }
        block()
    }
}