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
            TablePageObject.apply {
                Month("September").assertIsDisplayed()
                LeftArrowButton.assertIsDisplayed().performClick()
                Month("August").assertIsDisplayed()
                RightArrowButton.assertIsDisplayed().performClick()
                Month("September").assertIsDisplayed()
                RightArrowButton.assertIsDisplayed().performClick()
                Month("October").assertIsDisplayed()
            }
        }

    }

    @Test
    fun testDayOpen() {
        withRule {
            TablePageObject.apply {
                Day("1").assertIsDisplayed()
                Day("1").assertIsDisplayed().performClick()
            }
            CoffeeListPageObject.apply {
                CappuccinoItem.assertIsDisplayed()
                LatteItem.assertIsDisplayed()
            }
        }
    }

    private inline fun <R> withRule(block: ComposeTestRuleJUnit.() -> R): R =
        with(composeTestRule) {
            setContent {
                PagesContent(NavigationStore(), DaysCoffeesStore())
            }
            block()
        }
}

