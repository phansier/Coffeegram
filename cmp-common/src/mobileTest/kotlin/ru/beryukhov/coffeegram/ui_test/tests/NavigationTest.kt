package ru.beryukhov.coffeegram.ui_test.tests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import ru.beryukhov.coffeegram.ui_test.UiTest
import ru.beryukhov.coffeegram.ui_test.screens.DayListScreen
import ru.beryukhov.coffeegram.ui_test.screens.NavigationBar
import ru.beryukhov.coffeegram.ui_test.screens.SettingsScreen
import ru.beryukhov.coffeegram.ui_test.screens.StatsScreen
import ru.beryukhov.coffeegram.ui_test.screens.TableScreen
import ru.beryukhov.coffeegram.ui_test.setRootScreenContent
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class NavigationTest : UiTest() {

    @Test
    fun calendarTabIsSelectedOnStart() = runComposeUiTest {
        setRootScreenContent()

        NavigationBar(this).calendar.assertIsSelected()
        TableScreen(this).month.assertIsDisplayed()
    }

    @Test
    fun statsTabOpensStats() = runComposeUiTest {
        setRootScreenContent()

        NavigationBar(this).stats.apply {
            performClick()
            assertIsSelected()
        }
        StatsScreen(this).title.assertIsDisplayed()
    }

    @Test
    fun settingsTabOpensSettings() = runComposeUiTest {
        setRootScreenContent()

        NavigationBar(this).settings.apply {
            performClick()
            assertIsSelected()
        }
        SettingsScreen(this).styleSection.assertIsDisplayed()
    }

    @Test
    fun calendarTabReturnsToMonthTable() = runComposeUiTest {
        setRootScreenContent()

        NavigationBar(this).apply {
            stats.performClick()
            calendar.performClick()
            calendar.assertIsSelected()
        }
        TableScreen(this).month.assertIsDisplayed()
    }

    @Test
    fun backButtonClosesDayList() = runComposeUiTest {
        setRootScreenContent()
        TableScreen(this).day("1").performClick()

        DayListScreen(this).apply {
            assertIsDisplayed()
            backButton.performClick()
            assertDoesNotExist()
        }
        TableScreen(this).month.assertIsDisplayed()
    }
}
