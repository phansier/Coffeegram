package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slapps.cupertino.adaptive.AdaptiveIconButton
import com.slapps.cupertino.adaptive.ExperimentalAdaptiveApi
import com.slapps.cupertino.adaptive.Theme
import com.slapps.cupertino.adaptive.currentTheme

private val TopBarIconButtonSize = 38.dp

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TopBarIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // The chip's visual size/shape is drawn on this outer Box rather than passed into
    // AdaptiveIconButton's own modifier, so it isn't affected by the underlying IconButton's
    // own minimum-touch-target sizing (which previously made adjacent chips overlap/crowd).
    Box(
        modifier = modifier
            .size(TopBarIconButtonSize)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AdaptiveIconButton(onClick = onClick, content = content)
    }
}

@OptIn(ExperimentalAdaptiveApi::class)
@Composable
fun TopBarTitle(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    cupertinoTitle: String = title,
) {
    if (currentTheme == Theme.Cupertino) {
        Text(
            text = cupertinoTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier,
        )
    } else {
        Column(modifier = modifier) {
            if (eyebrow != null) {
                TopBarEyebrowText(eyebrow)
            }
            TopBarTitleText(title)
        }
    }
}

@Composable
private fun TopBarEyebrowText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun TopBarTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
