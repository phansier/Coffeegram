package ru.beryukhov.coffeegram.newapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.CupertinoText
import com.slapps.cupertino.adaptive.AdaptiveButton
import com.slapps.cupertino.adaptive.AdaptiveScaffold
import com.slapps.cupertino.adaptive.AdaptiveSwitch
import com.slapps.cupertino.adaptive.AdaptiveTopAppBar
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.section.CupertinoSection
import com.slapps.cupertino.section.sectionTitle

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TableScreen(
    component: TableComponent,
    modifier: Modifier = Modifier,
) {
    AdaptiveScaffold(
        modifier = modifier,
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
            AdaptiveButton(onClick = { component.onNavigate(RootComponent.Child.Settings::class) }) {
                Text("Go To Settings")
            }
        }
    }
}
