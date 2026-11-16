package ru.beryukhov.coffeegram.ui_test.tests

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import ru.beryukhov.coffeegram.ui_test.UiTest
import ru.beryukhov.coffeegram.ui_test.screens.CoffeeItem
import ru.beryukhov.coffeegram.ui_test.screens.DayListScreen
import ru.beryukhov.coffeegram.ui_test.screens.TableScreen
import ru.beryukhov.coffeegram.ui_test.setRootScreenContent
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DayListTest : UiTest() {

    @Test
    fun plusAndMinusChangeCoffeeCount() = runComposeUiTest {
        openCappuccino().apply {
            assertCount(0)

            incrementButton.performClick()
            incrementButton.performClick()
            assertCount(2)

            decrementButton.performClick()
            assertCount(1)
        }
    }

    @Test
    fun minusIsDisabledAtZero() = runComposeUiTest {
        openCappuccino().apply {
            decrementButton.assertIsNotEnabled()

            incrementButton.performClick()
            decrementButton.assertIsEnabled()

            decrementButton.performClick()
            decrementButton.assertIsNotEnabled()
        }
    }

    private fun ComposeUiTest.openCappuccino(): CoffeeItem {
        setRootScreenContent()
        TableScreen(this).day("1").performClick()
        return DayListScreen(this).coffeeItem("Cappuccino")
    }
}
