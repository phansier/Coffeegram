package ru.beryukhov.coffeegram.view

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.onSecondaryClick(onClick: () -> Unit): Modifier =
    pointerInput(onClick) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.isSecondaryPress()) {
                    event.changes.forEach { it.consume() }
                    onClick()
                }
            }
        }
    }

private fun PointerEvent.isSecondaryPress(): Boolean =
    type == PointerEventType.Press && buttons.isSecondaryPressed
