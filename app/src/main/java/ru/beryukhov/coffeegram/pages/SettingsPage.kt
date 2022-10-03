@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.BuildConfig
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.AppTypography
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DarkThemeState
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.model.getThemeStoreStub

@Preview
@Composable
private fun SettingsPagePreview() {
    CoffeegramTheme {
        Scaffold {
            Column(modifier = Modifier.padding(it)) {
                SettingsPage(getThemeStoreStub(LocalContext.current))
            }
        }
    }
}

@Composable
fun ColumnScope.SettingsPage(
    themeStore: ThemeStore,
    modifier: Modifier = Modifier,
    startWearableActivity: () -> Unit = {}
) {
    Column(modifier = modifier.weight(1f)) {
        Text(
            stringResource(R.string.app_theme),
            style = AppTypography.titleMedium,
            modifier = Modifier.absolutePadding(left = 24.dp, top = 16.dp)
        )
        val themeState: ThemeState by themeStore.state.collectAsState()
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = { themeStore.newIntent(ThemeIntent.SetSystemIntent) },
            stringResource(R.string.app_theme_system)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = { themeStore.newIntent(ThemeIntent.SetLightIntent) },
            stringResource(R.string.app_theme_light)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = { themeStore.newIntent(ThemeIntent.SetDarkIntent) },
            stringResource(R.string.app_theme_dark)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ThemeCheckBoxWithText(
                checked = themeState.isDynamic,
                onCheckedChange = {
                    themeStore.newIntent(
                        if (it) ThemeIntent.SetDynamicIntent else ThemeIntent.UnSetDynamicIntent
                    )
                },
                stringResource(R.string.app_theme_dynamic)
            )
        }
        Divider()
        if (BuildConfig.DEBUG) {
            Button(onClick = { startWearableActivity() }, modifier = Modifier.padding(16.dp)) {
                Text("Start Wearable Activity")
            }
        }
    }
}

@Composable
fun SettingsAppBar(modifier: Modifier = Modifier,) {
    SmallTopAppBar(modifier = modifier, title = { Text(stringResource(R.string.settings)) }
    )
}

@Composable
fun ThemeRadioButtonWithText(
    selected: Boolean,
    onClick: (() -> Unit)?,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        RadioButton(selected = selected, onClick = onClick, modifier = Modifier.align(CenterVertically))
        Text(text = label, modifier = Modifier.align(CenterVertically))
    }
}

@Composable
fun ThemeCheckBoxWithText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.align(CenterVertically))
        Text(text = label, modifier = Modifier.align(CenterVertically))
    }
}
