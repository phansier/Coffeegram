package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test


class ExampleComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule(/*disableTransitions = true*/)

    @Test
    fun testYear() {
        with(composeTestRule) {
            setContent {
                PagesContent(navigationStore = NavigationStore(), daysCoffeesStore = DaysCoffeesStore())
            }
            onNodeWithText("2020"  ).assertIsDisplayed()
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

    private inline fun <R> withRule(block: ComposeTestRule.() -> R): R =
        with(composeTestRule) {
            setContent {
                PagesContent(
                    navigationStore = NavigationStore(),
                    daysCoffeesStore = DaysCoffeesStore()
                )
            }
            block()
        }
}

