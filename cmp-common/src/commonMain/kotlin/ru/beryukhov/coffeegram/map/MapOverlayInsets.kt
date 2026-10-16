package ru.beryukhov.coffeegram.map

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable

@Composable
fun mapOverlayInsets(sides: WindowInsetsSides): WindowInsets =
    WindowInsets.systemBars.union(WindowInsets.displayCutout).only(sides)
