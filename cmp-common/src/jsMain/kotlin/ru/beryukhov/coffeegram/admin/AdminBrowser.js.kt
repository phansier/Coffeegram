package ru.beryukhov.coffeegram.admin

internal actual fun locationSearch(): String = js(JS_LOCATION_SEARCH) as String
