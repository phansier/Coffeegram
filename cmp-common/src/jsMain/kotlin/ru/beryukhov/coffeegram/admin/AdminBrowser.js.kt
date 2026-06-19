package ru.beryukhov.coffeegram.admin

internal actual fun locationSearch(): String = js(JS_LOCATION_SEARCH) as String

internal actual fun locationHash(): String = js(JS_LOCATION_HASH) as String

internal actual fun locationOrigin(): String = js(JS_LOCATION_ORIGIN) as String

internal actual fun replaceUrl(url: String) {
    js(JS_REPLACE_STATE)
}

internal actual fun storageGet(key: String): String = js(JS_STORAGE_GET) as String

internal actual fun storageSet(key: String, value: String) {
    js(JS_STORAGE_SET)
}

internal actual fun storageRemove(key: String) {
    js(JS_STORAGE_REMOVE)
}
