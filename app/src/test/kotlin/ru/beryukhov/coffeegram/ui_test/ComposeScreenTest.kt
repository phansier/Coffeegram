package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.beryukhov.coffeegram.PreviewContextConfigurationEffectProvider
import ru.beryukhov.coffeegram.TestApplication
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import ru.beryukhov.coffeegram.screens.RootScreen

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
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
        withRule {
            onComposeScreen<TableScreen> {
                monthName.assertIsDisplayed()
            }
        }
    }

    @Test
    fun testMonthChange() {
        withRule {
            onComposeScreen<TableScreen> {
                monthName.assertIsDisplayed()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testPageKeysChangeMonthFromFocusedDay() {
        withRule {
            val initialMonth = monthTitle()
            val firstDay = onAllNodesWithTag("Day")[0]
            firstDay.requestFocus()

            firstDay.performKeyInput { pressKey(Key.PageDown) }
            assertNotEquals(initialMonth, monthTitle())

            firstDay.performKeyInput { pressKey(Key.PageUp) }
            assertEquals(initialMonth, monthTitle())
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testArrowKeysMoveFocusBetweenDays() {
        withRule {
            val days = onAllNodesWithTag("Day")
            days[0].requestFocus()

            days[0].performKeyInput { pressKey(Key.DirectionRight) }
            days[1].assertIsFocused()

            days[1].performKeyInput { pressKey(Key.DirectionLeft) }
            days[0].assertIsFocused()
        }
    }

    private fun ComposeTestRule.monthTitle(): String =
        onNodeWithTag("Month", useUnmergedTree = true)
            .onChildren()
            .fetchSemanticsNodes()
            .flatMap { node -> node.config.getOrElse(SemanticsProperties.Text) { emptyList() } }
            .joinToString(separator = " ") { it.text }

    private inline fun <R> withRule(block: ComposeTestRule.() -> R): R =
        with(composeTestRule) {
            setContent {
                PreviewContextConfigurationEffectProvider()

                val lifecycle = LifecycleRegistry()
                val componentContext = DefaultComponentContext(lifecycle)
                val themeStore = ThemeStore(ThemeInMemoryStorage())
                val daysCoffeesStore = object : DaysCoffeesStore {
                    override val state: StateFlow<DaysCoffeesState> = MutableStateFlow(DaysCoffeesState())
                    override fun newIntent(intent: DaysCoffeesIntent) = Unit
                }

                val rootComponent = DefaultRootComponent(
                    context = componentContext,
                    themeStore = themeStore,
                    daysCoffeesStore = daysCoffeesStore,
                    showMap = false,
                    onAndroidStartWearableActivity = MutableStateFlow(null),
                    onAndroidIconChange = {},
                )

                RootScreen(rootComponent = rootComponent)
            }
            block()
        }
}
