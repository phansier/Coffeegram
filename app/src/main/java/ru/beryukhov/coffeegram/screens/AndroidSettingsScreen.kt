@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.BuildConfig
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.components.AndroidSettingsComponent
import ru.beryukhov.coffeegram.model.DarkThemeState

@Composable
fun AndroidSettingsScreen(
    component: AndroidSettingsComponent,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val themeState by component.models.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.app_theme),
            style = typography.titleMedium,
            modifier = Modifier.absolutePadding(left = 24.dp, top = 16.dp)
        )

        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.SYSTEM,
            onClick = { component.onSetSystemTheme() },
            stringResource(R.string.app_theme_system)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.LIGHT,
            onClick = { component.onSetLightTheme() },
            stringResource(R.string.app_theme_light)
        )
        ThemeRadioButtonWithText(
            selected = themeState.useDarkTheme == DarkThemeState.DARK,
            onClick = { component.onSetDarkTheme() },
            stringResource(R.string.app_theme_dark)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scope = rememberCoroutineScope()
            ThemeCheckBoxWithText(
                checked = themeState.isDynamic == true,
                onCheckedChange = {
                    if (it) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Now app theme will follow system theme",
                                actionLabel = "OK",
                            )
                        }
                    }
                    component.onSetDynamicTheme(it)
                },
                stringResource(R.string.app_theme_dynamic)
            )
        }

        val scope = rememberCoroutineScope()
        ThemeCheckBoxWithText(
            checked = themeState.isSummer == true,
            onCheckedChange = {
                if (it) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Summer starting",
                            actionLabel = "OK",
                        )
                    }
                }
                component.onSetSummerTheme(it)
            },
            stringResource(R.string.app_theme_summer)
        )

        HorizontalDivider()

        if (BuildConfig.DEBUG) {
            Button(
                onClick = { component.onStartWearableActivity() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start Wearable Activity")
            }
        }
    }
}

@Composable
fun AndroidSettingsAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings)) },
        modifier = modifier
    )
}

@Composable
private fun ThemeRadioButtonWithText(
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
private fun ThemeCheckBoxWithText(
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
