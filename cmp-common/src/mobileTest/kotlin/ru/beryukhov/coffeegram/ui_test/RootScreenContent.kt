package ru.beryukhov.coffeegram.ui_test

import androidx.compose.runtime.remember
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.ThemeInMemoryStorage
import ru.beryukhov.coffeegram.screens.RootScreen

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.setRootScreenContent() {
    setContent {
        TestEnvironment {
            val rootComponent = remember {
                DefaultRootComponent(
                    context = DefaultComponentContext(LifecycleRegistry()),
                    themeStore = ThemeStore(ThemeInMemoryStorage()),
                    daysCoffeesStore = EmptyDaysCoffeesStore(),
                )
            }
            RootScreen(rootComponent = rootComponent)
        }
    }
}

private class EmptyDaysCoffeesStore : DaysCoffeesStore {
    override val state: StateFlow<DaysCoffeesState> = MutableStateFlow(DaysCoffeesState())
    override fun newIntent(intent: DaysCoffeesIntent) = Unit
}
