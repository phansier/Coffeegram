package ru.beryukhov.coffeegram.ui_test

import androidx.compose.runtime.Composable

expect abstract class UiTest()

@Composable
expect fun TestEnvironment(content: @Composable () -> Unit)
