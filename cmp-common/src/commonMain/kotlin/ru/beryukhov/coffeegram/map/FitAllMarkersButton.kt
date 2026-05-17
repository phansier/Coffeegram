package ru.beryukhov.coffeegram.map

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Floating action button used by both `MapScreen` implementations to fit the camera to all
 * coffee-shop markers.
 */
@Composable
fun FitAllMarkersButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.padding(16.dp),
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = "",
        )
    }
}
