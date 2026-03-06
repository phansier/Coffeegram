package ru.beryukhov.coffeegram.ui_test

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.github.kakaocup.compose.rule.KakaoComposeTestRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import ru.beryukhov.coffeegram.PreviewContextConfigurationEffectProvider
import ru.beryukhov.coffeegram.components.DefaultAndroidRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import ru.beryukhov.coffeegram.screens.AndroidRootScreen

@RunWith(RobolectricTestRunner::class)
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

                val rootComponent = DefaultAndroidRootComponent(
                    context = componentContext,
                    themeStore = themeStore,
                    daysCoffeesStore = daysCoffeesStore,
                    showMap = false,
                    onStartWearableActivity = {},
                    onIconChange = {},
                )

                AndroidRootScreen(rootComponent = rootComponent)
            }
            block()
        }
}
