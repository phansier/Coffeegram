package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.kakaocup.compose.node.element.KNode
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.view.MonthTable
import ru.beryukhov.date_time_utils.YearMonth
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MonthTableSmokeTest : UiTest() {

    @Test
    fun dayOfMonthIsDisplayed() = runComposeUiTest {
        setContent {
            TestEnvironment {
                MonthTable(
                    yearMonth = YearMonth(2020, Month.JULY),
                    today = LocalDate(2020, 7, 14),
                    filledDayItemsMap = persistentMapOf(),
                    onClick = {},
                )
            }
        }

        day(this, "14").assertIsDisplayed()
    }

    private fun day(semanticsProvider: SemanticsNodeInteractionsProvider, number: String) =
        KNode(semanticsProvider) {
            hasTestTag("Day")
            hasText(number)
        }
}
