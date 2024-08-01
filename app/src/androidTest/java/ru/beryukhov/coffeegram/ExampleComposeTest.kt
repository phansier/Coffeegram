package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Month
import org.junit.Rule
import org.junit.Test
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

class ExampleComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testYear() {
        withRule(yearMonth = YearMonth(2020, Month(1))) {
            onNodeWithText("2020").assertIsDisplayed()
        }
    }

    @Test
    fun testMonthChange() {
        withRule(yearMonth = YearMonth(2020, Month(9))) {
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
    fun testDayOpen() = runTest {
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

    private inline fun <R> withRule(yearMonth: YearMonth = nowYM(), block: ComposeTestRule.() -> R): R =
        with(composeTestRule) {
            setContent {
                PagesContent(
                    navigationStore = NavigationStore(yearMonth = yearMonth),
                )
            }
            block()
        }
}
