package ru.beryukhov.coffeegram.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveSwitch
import com.slapps.cupertino.adaptive.AdaptiveWidget
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun ThemeRadioButtonWithText(
    selected: Boolean,
    onClick: (() -> Unit)?,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .let {
                if (onClick != null) {
                    it.selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                } else {
                    it
                }
            }
            .padding(8.dp),
        verticalAlignment = CenterVertically,
    ) {
        AdaptiveRadioButton(selected = selected)
        Text(
            text = label,
            style = typography.bodyMedium,
        )
    }
}

/**
 * Adaptive single-choice indicator: a Material [RadioButton] under the Material theme, and an
 * Apple-style radio button under the Cupertino theme (cupertino-adaptive has no radio button, so
 * we compose one via [AdaptiveWidget], mirroring how [AdaptiveSwitch] is built).
 *
 * Apple's radio button is a small circle: a hollow, bordered circle when unselected; an
 * accent-filled circle with a white center dot when selected.
 */
@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun AdaptiveRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    AdaptiveWidget(
        material = {
            RadioButton(selected = selected, onClick = null, modifier = modifier)
        },
        cupertino = {
            val accent = MaterialTheme.colorScheme.primary
            val onAccent = MaterialTheme.colorScheme.onPrimary
            val outline = MaterialTheme.colorScheme.outline
            // 48dp footprint matches the Material RadioButton so rows align and don't shift.
            Box(modifier = modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val outer = size.minDimension / 2f
                    if (selected) {
                        drawCircle(color = accent, radius = outer)
                        drawCircle(color = onAccent, radius = outer * 0.4f)
                    } else {
                        val stroke = 1.5.dp.toPx()
                        drawCircle(color = outline, radius = outer - stroke / 2f, style = Stroke(width = stroke))
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun ThemeSwitchWithText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Text(
            text = label,
            style = typography.bodyMedium,
            modifier = Modifier.weight(1f).align(CenterVertically)
        )
        AdaptiveSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.align(CenterVertically)
        )
    }
}
