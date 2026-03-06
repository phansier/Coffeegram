package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.kakaocup.compose.KakaoCompose
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import repository.CoffeeRepository
import repository.InMemoryCoffeeRepository
import ru.beryukhov.coffeegram.MainActivity

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Ignore("todo fix")
class ComposeAppTest {
    @get:Rule
    val composeTestRule by lazy {
        replaceRoomWithInMemoryStorage()
        createAndroidComposeRule<MainActivity>()
    }

    @get:Rule
    val kakaoComposeTestRule by lazy {
        KakaoComposeTestRule(composeTestRule)
    }

    @Before
    fun setUp() {
        KakaoCompose.Override.useUnmergedTree = true
    }

    @After
    fun tearDown() {
        stopKoin()
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

internal fun replaceRoomWithInMemoryStorage() {
    val testModule = module {
        single<CoffeeRepository> { InMemoryCoffeeRepository() }
    }

    loadKoinModules(testModule)
}
