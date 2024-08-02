package ru.beryukhov.coffeegram

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

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
            assertIsDisplayed()
            coffeeList.assertLengthEquals(42)
//                CappuccinoItem.assertIsDisplayed()
//                LatteItem.assertIsDisplayed()
        }
        }
}
