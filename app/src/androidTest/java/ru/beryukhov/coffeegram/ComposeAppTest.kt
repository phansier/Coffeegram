package ru.beryukhov.coffeegram

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class ComposeAppTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @Test
    fun testDayOpen() {
        ComposeScreen.onComposeScreen<TableScreen>(composeTestRule) {
            assertIsDisplayed()
            day("1").apply {
                assertIsDisplayed()
                performClick()
            }
        }
        ComposeScreen.onComposeScreen<CoffeeListScreen>(composeTestRule) {
            // composeTestRule.onRoot().printToLog("TEST_")
            assertIsDisplayed()
            coffeeList.assertLengthEquals(12)
            coffeeList.childAt<CoffeeItemNode>(0) {
                title.assertTextEquals("Cappuccino")
            }
            coffeeList.childAt<CoffeeItemNode>(1) {
                title.assertTextEquals("Latte")
            }
        }
        }
}
