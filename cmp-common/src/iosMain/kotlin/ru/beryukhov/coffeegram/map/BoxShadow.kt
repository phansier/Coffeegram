package ru.beryukhov.coffeegram.map

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

/**
 * iOS implementation: shadows are not rendered.
 *
 * Markers are rasterized via a graphics layer into a `UIImage` before being attached to the
 * `MKMapView`, so a true blur shadow isn't critical for the marker visuals.
 */
@Stable
actual fun Modifier.boxShadow(
    color: Color,
    blurRadius: Dp,
    spreadRadius: Dp,
    offset: DpOffset,
    shape: Shape,
    clip: Boolean,
    inset: Boolean
): Modifier = this
