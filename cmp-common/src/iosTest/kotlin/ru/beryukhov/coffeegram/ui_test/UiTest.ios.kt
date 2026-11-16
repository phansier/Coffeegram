package ru.beryukhov.coffeegram.ui_test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import platform.UIKit.UIViewController

actual abstract class UiTest actual constructor()

@Composable
actual fun TestEnvironment(content: @Composable () -> Unit) {
    val viewController = remember { UIViewController() }
    CompositionLocalProvider(LocalUIViewController provides viewController, content = content)
}
