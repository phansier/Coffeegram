package ru.beryukhov.coffeegram.newapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.AdaptiveSwitch
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.section.CupertinoSection
import com.slapps.cupertino.section.sectionTitle
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeState

@Composable
@Suppress("ModifierMissing")
fun NewApp(rootComponent: RootComponent) {

    CoffeegramTheme(
        themeState = ThemeState(useDarkTheme = DarkThemeState.SYSTEM, isCupertino = rootComponent.isMaterial.value)
    ) {
        Children(
            stack = rootComponent.stack,
            modifier = Modifier.fillMaxSize(),
            ) { child ->
                when (val c = child.instance) {
                    is RootComponent.Child.Table -> TableScreen(c.component)
                }
            }
    }
}

@Composable
@Suppress("ModifierMissing")
fun TableScreen(
    component: TableComponent
) {
    AdaptiveScaffold(
        topBar = {
            AdaptiveTopAppBar(
                title = {
                    Text("Adaptive")
                },
                actions = {
                    Text("Theme")
                    AdaptiveSwitch(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        checked = component.isMaterial.value,
                        onCheckedChange = {
                            component.onThemeChanged()
                        },
                    )
                },
            )
        }
    ) {
        CupertinoSection(
            title = {
                CupertinoText(
                    text = "Controls".sectionTitle(),
                )
            }
        ) {
            Text("Toggle layout direction")
        }
    }
}
