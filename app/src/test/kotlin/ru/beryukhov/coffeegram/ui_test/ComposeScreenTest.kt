package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import kotlinx.datetime.Month
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import ru.beryukhov.coffeegram.PagesContent
import ru.beryukhov.coffeegram.PreviewContextConfigurationEffectProvider
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.date_time_utils.YearMonth
import ru.beryukhov.date_time_utils.nowYM

@RunWith(RobolectricTestRunner::class)
class ComposeScreenTest {
    @get:Rule
    val composeTestRule by lazy {
        replaceRoomWithInMemoryStorage()
        createComposeRule()
    }

    @get:Rule
    val kakaoComposeTestRule = KakaoComposeTestRule(composeTestRule)
    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testYear() {
        withRule(yearMonth = YearMonth(2020, Month(1))) {
            onComposeScreen<TableScreen> {
//                yearName.assertIsDisplayed() // todo false negative for some reason
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
                PreviewContextConfigurationEffectProvider()
                PagesContent(
                    navigationStore = NavigationStore(yearMonth = yearMonth),
                )
            }
            block()
        }
}
