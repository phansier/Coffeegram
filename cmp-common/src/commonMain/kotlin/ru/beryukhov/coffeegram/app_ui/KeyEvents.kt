package ru.beryukhov.coffeegram.app_ui

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

internal inline fun KeyEvent.handleKeyDown(keys: Set<Key>, action: () -> Unit): Boolean =
    (type == KeyEventType.KeyDown && key in keys).also { handled -> if (handled) action() }

internal inline fun KeyEvent.handleKeyDown(key: Key, action: () -> Unit): Boolean =
    handleKeyDown(setOf(key), action)

internal fun KeyEvent.arrowFocusDirection(): FocusDirection? =
    if (type != KeyEventType.KeyDown) {
        null
    } else {
        when (key) {
            Key.DirectionLeft -> FocusDirection.Left
            Key.DirectionRight -> FocusDirection.Right
            Key.DirectionUp -> FocusDirection.Up
            Key.DirectionDown -> FocusDirection.Down
            else -> null
        }
    }
