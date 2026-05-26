package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

interface SettingsComponent {
    val models: StateFlow<ThemeState>

    val onAndroidStartWearableActivity: StateFlow<(() -> Unit)?>
    val onAndroidIconChange: (isSummer: Boolean) -> Unit

    fun onSetSystemTheme()
    fun onSetLightTheme()
    fun onSetDarkTheme()

    fun onSetCupertinoTheme(enabled: Boolean)
    fun onSetDynamicTheme(enabled: Boolean)
    fun onSetSummerTheme(enabled: Boolean)
}

class DefaultSettingsComponent(
    context: ComponentContext,
    val themeStore: ThemeStore,
    override val onAndroidStartWearableActivity: StateFlow<(() -> Unit)?>,
    override val onAndroidIconChange: (isSummer: Boolean) -> Unit,
) : SettingsComponent, ComponentContext by context {
    override val models: StateFlow<ThemeState> = themeStore.state

    override fun onSetSystemTheme() {
        themeStore.newIntent(ThemeIntent.SetSystemIntent)
    }

    override fun onSetLightTheme() {
        themeStore.newIntent(ThemeIntent.SetLightIntent)
    }

    override fun onSetDarkTheme() {
        themeStore.newIntent(ThemeIntent.SetDarkIntent)
    }

    override fun onSetCupertinoTheme(enabled: Boolean) {
        themeStore.newIntent(ThemeIntent.SetCupertinoIntent(enabled))
    }

    override fun onSetDynamicTheme(enabled: Boolean) {
        themeStore.newIntent(ThemeIntent.SetDynamicIntent(enabled))
    }

    override fun onSetSummerTheme(enabled: Boolean) {
        // Update the icon when summer theme is toggled
        onAndroidIconChange(enabled)
        themeStore.newIntent(ThemeIntent.SetSummerIntent(enabled))
    }
}
