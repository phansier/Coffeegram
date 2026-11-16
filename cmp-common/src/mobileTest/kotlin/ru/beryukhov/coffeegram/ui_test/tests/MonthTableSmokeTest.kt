package ru.beryukhov.coffeegram.ui_test.tests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ru.beryukhov.coffeegram.ui_test.TestEnvironment
import ru.beryukhov.coffeegram.ui_test.UiTest
import ru.beryukhov.coffeegram.ui_test.screens.TableScreen
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

        TableScreen(this).day("14").assertIsDisplayed()
    }
}
