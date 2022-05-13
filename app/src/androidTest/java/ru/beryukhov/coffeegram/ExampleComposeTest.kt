package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.YearMonth
import ru.beryukhov.coffeegram.model.NavigationStore

class ExampleComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testYear() {
        with(composeTestRule) {
            setContent {
                PagesContent(
                    navigationStore = NavigationStore(yearMonth = YearMonth.of(2020, 1)),
                )
            }
            onNodeWithText("2020").assertIsDisplayed()
        }
    }

    @Test
    @Ignore
    fun testMonthChange() {
        withRule(yearMonth = YearMonth.of(2020, 9)) {
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
    @Ignore
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

    private inline fun <R> withRule(yearMonth: YearMonth = YearMonth.now(), block: ComposeTestRule.() -> R): R =
        with(composeTestRule) {
            setContent {
                PagesContent(
                    navigationStore = NavigationStore(yearMonth = yearMonth),
                )
            }
            block()
        }
}
