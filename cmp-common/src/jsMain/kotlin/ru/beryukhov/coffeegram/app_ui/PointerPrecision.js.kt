package ru.beryukhov.coffeegram.app_ui

internal actual fun matchesFinePointerMedia(): Boolean = js(JS_FINE_POINTER_QUERY) as Boolean
