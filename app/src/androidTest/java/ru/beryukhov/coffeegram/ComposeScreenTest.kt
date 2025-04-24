package ru.beryukhov.coffeegram

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import kotlinx.datetime.Month
import org.junit.Rule
import org.junit.Test
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

class ComposeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val kakaoComposeTestRule = KakaoComposeTestRule(composeTestRule)

    @Test
    fun testYear() {
        withRule(yearMonth = YearMonth(2020, Month(1))) {
            onNodeWithText("2020").assertIsDisplayed()
            onComposeScreen<TableScreen> {
                yearName.assertIsDisplayed()
                monthName.assertIsDisplayed()
                yearName.assertTextEquals("2020")
                monthName.assertTextEquals("January")
            }
        }
    }

    @Test
    fun testMonthChange() {
        withRule(yearMonth = YearMonth(2020, Month(9))) {
            onComposeScreen<TableScreen> {
                monthName.assertTextEquals("September")
                leftArrowButton {
                    assertIsDisplayed()
                    performClick()
                }
                monthName.assertTextEquals("August")
                rightArrowButton {
                    assertIsDisplayed()
                    performClick()
                }
                monthName.assertTextEquals("September")
                rightArrowButton {
                    assertIsDisplayed()
                    performClick()
                }
                monthName.assertTextEquals("October")
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
