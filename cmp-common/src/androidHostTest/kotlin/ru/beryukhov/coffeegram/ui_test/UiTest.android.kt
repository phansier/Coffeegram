package ru.beryukhov.coffeegram.ui_test

import androidx.compose.runtime.Composable
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ru.beryukhov.coffeegram.PreviewContextConfigurationEffectProvider

@RunWith(RobolectricTestRunner::class)
actual abstract class UiTest actual constructor()

@Composable
actual fun TestEnvironment(content: @Composable () -> Unit) {
    PreviewContextConfigurationEffectProvider()
    content()
}
