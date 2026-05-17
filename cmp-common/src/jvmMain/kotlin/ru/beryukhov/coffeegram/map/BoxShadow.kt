package ru.beryukhov.coffeegram.map

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

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
