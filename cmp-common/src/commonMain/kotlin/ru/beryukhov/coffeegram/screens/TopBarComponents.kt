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

@Composable
fun TopBarTitle(
    title: String,
    eyebrow: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (eyebrow != null) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
