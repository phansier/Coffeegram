package ru.beryukhov.coffeegram

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.kakaocup.compose.KakaoCompose
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class ComposeAppTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val kakaoComposeTestRule = KakaoComposeTestRule(composeTestRule)

    @Before
    fun setUp() {
        KakaoCompose.Override.useUnmergedTree = true
    }

    @Test
    fun testDayOpen() {
        onComposeScreen<TableScreen> {
            assertIsDisplayed()
            day("1").apply {
                assertIsDisplayed()
                performClick()
            }
        }
        onComposeScreen<CoffeeListScreen> {
            assertIsDisplayed()
            coffeeList.assertLengthEquals(12)
            coffeeList.childAt<CoffeeItemNode>(0) {
                hasText("Cappuccino")
            }
            coffeeList.childAt<CoffeeItemNode>(1) {
                hasText("Latte")
            }
        }
    }
}
