package ru.beryukhov.coffeegram.app_ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.mediaQuery

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMediaQueryApi::class)
@Composable
internal actual fun hasFinePointer(): Boolean =
    ComposeUiFlags.isMediaQueryIntegrationEnabled &&
        mediaQuery { pointerPrecision == UiMediaScope.PointerPrecision.Fine }
