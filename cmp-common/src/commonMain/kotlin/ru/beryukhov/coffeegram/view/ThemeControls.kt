package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveSwitch
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi

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

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun ThemeSwitchWithText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(24.dp)) {
        Text(text = label, modifier = Modifier.weight(1f).align(CenterVertically))
        AdaptiveSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.align(CenterVertically)
        )
    }
}
