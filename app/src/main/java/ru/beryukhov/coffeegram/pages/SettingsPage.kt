package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.app_ui.typography
import ru.beryukhov.coffeegram.model.ThemeIntent
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore


@Preview
@Composable
fun SettingsPagePreview() {
    CoffeegramTheme {
        Scaffold {
            Column {
                SettingsPage(ThemeStore(LocalContext.current))
            }
        }
    }
}

@Composable
fun ColumnScope.SettingsPage(themeStore: ThemeStore) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            stringResource(R.string.app_theme),
            style = typography.h6,
            modifier = Modifier.padding(8.dp)
        )
        val themeState: ThemeState by themeStore.state.collectAsState()
        ThemeRadioButtonWithText(
            selected = themeState == ThemeState.SYSTEM,
            onClick = { themeStore.newIntent(ThemeIntent.SetSystemIntent) },
            stringResource(R.string.app_theme_system)
        )
        ThemeRadioButtonWithText(
            selected = themeState == ThemeState.LIGHT,
            onClick = { themeStore.newIntent(ThemeIntent.SetLightIntent) },
            stringResource(R.string.app_theme_light)
        )
        ThemeRadioButtonWithText(
            selected = themeState == ThemeState.DARK,
            onClick = { themeStore.newIntent(ThemeIntent.SetDarkIntent) },
            stringResource(R.string.app_theme_dark)
        )
    }
}

@Composable
fun SettingsAppBar() {
    TopAppBar(title = { Text(stringResource(R.string.settings)) }
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
        RadioButton(selected = selected, onClick = onClick)
        Text(label)
    }
}
