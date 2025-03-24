package ru.beryukhov.coffeegram.newapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState

@Composable
@Suppress("ModifierMissing")
fun NewApp(rootComponent: RootComponent) {

    CoffeegramTheme(
        themeState = ThemeState(useDarkTheme = DarkThemeState.SYSTEM, isCupertino = rootComponent.isMaterial.value)
    ) {
        Column {
            Children(
                stack = rootComponent.stack,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) { child ->
                when (val c = child.instance) {
                    is RootComponent.Child.Table -> TableScreen(c.component)
                    is RootComponent.Child.Settings -> SettingsScreen(c.component)
                }
            }
//            Children(
//                stack = rootComponent.stack,
//                modifier = Modifier.fillMaxWidth().weight(1f),
//            ) { child ->
//                when (val c = child.instance) {
//                    is RootComponent.Child.Table -> TableScreen(c.component)
//                }
//            }
        }
    }
}
