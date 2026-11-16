package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class RootScreenTest : UiTest() {

    @Test
    fun monthIsDisplayed() = runComposeUiTest {
        setRootScreenContent()

        TableScreen(this).month.assertIsDisplayed()
    }

    @Test
    fun arrowButtonsChangeMonth() = runComposeUiTest {
        setRootScreenContent()

        TableScreen(this).apply {
            val initialMonth = monthTitle()

            nextMonthButton.performClick()
            assertNotEquals(initialMonth, monthTitle())

            previousMonthButton.performClick()
            assertEquals(initialMonth, monthTitle())
        }
    }

    @Test
    fun pageKeysChangeMonthFromFocusedDay() = runComposeUiTest {
        setRootScreenContent()

        TableScreen(this).apply {
            val initialMonth = monthTitle()
            val firstDay = dayAt(0)
            firstDay.requestFocus()

            firstDay.performKeyInput { pressKey(Key.PageDown) }
            assertNotEquals(initialMonth, monthTitle())

            firstDay.performKeyInput { pressKey(Key.PageUp) }
            assertEquals(initialMonth, monthTitle())
        }
    }

    @Test
    fun arrowKeysMoveFocusBetweenDays() = runComposeUiTest {
        setRootScreenContent()

        TableScreen(this).apply {
            dayAt(1).requestFocus()

            dayAt(1).performKeyInput { pressKey(Key.DirectionRight) }
            dayAt(2).assertIsFocused()

            dayAt(2).performKeyInput { pressKey(Key.DirectionLeft) }
            dayAt(1).assertIsFocused()
        }
    }
}
