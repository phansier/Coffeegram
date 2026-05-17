package ru.beryukhov.coffeegram.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.beryukhov.coffeegram.components.MapComponent

// The MapLibre wasmJs implementation lives in `MapScreen.wasmJs.kt`. The legacy JS target keeps
// the screen as a no-op for now.
@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
) = Unit
