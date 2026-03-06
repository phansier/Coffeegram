package ru.beryukhov.coffeegram.components

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore

/**
 * Extended settings component for Android-specific features.
 * Adds wearable activity start and dynamic icon change capabilities.
 */
interface AndroidSettingsComponent : SettingsComponent {
    fun onStartWearableActivity()
    fun onIconChange(isSummer: Boolean)
}

class DefaultAndroidSettingsComponent(
    context: ComponentContext,
    private val themeStore: ThemeStore,
    private val onStartWearableActivity: () -> Unit,
    private val onIconChange: (isSummer: Boolean) -> Unit,
) : AndroidSettingsComponent, ComponentContext by context {

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
        onIconChange(enabled)
        themeStore.newIntent(ThemeIntent.SetSummerIntent(enabled))
    }

    override fun onStartWearableActivity() {
        onStartWearableActivity.invoke()
    }

    override fun onIconChange(isSummer: Boolean) {
        onIconChange.invoke(isSummer)
    }
}
