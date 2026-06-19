package ru.beryukhov.coffeegram.admin

internal const val JS_LOCATION_SEARCH = "window.location.search"
internal const val JS_LOCATION_HASH = "window.location.hash"
internal const val JS_LOCATION_ORIGIN = "window.location.origin"
internal const val JS_REPLACE_STATE = "window.history.replaceState(null, '', url)"
internal const val JS_STORAGE_GET = "(window.localStorage.getItem(key) || '')"
internal const val JS_STORAGE_SET = "window.localStorage.setItem(key, value)"
internal const val JS_STORAGE_REMOVE = "window.localStorage.removeItem(key)"

internal expect fun locationSearch(): String

internal expect fun locationHash(): String

internal expect fun locationOrigin(): String

internal expect fun replaceUrl(url: String)

internal expect fun storageGet(key: String): String

internal expect fun storageSet(key: String, value: String)

internal expect fun storageRemove(key: String)
