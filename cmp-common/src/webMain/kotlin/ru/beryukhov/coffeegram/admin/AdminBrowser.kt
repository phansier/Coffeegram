package ru.beryukhov.coffeegram.admin

internal const val JS_LOCATION_SEARCH = "window.location.search"

/** `window.location.search`, e.g. "?admin" — used to route into the admin panel. */
internal expect fun locationSearch(): String
