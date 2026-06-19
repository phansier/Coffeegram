@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@file:Suppress("MatchingDeclarationName")

package ru.beryukhov.coffeegram.admin

internal actual fun locationSearch(): String = js(JS_LOCATION_SEARCH)

internal actual fun locationHash(): String = js(JS_LOCATION_HASH)

internal actual fun locationOrigin(): String = js(JS_LOCATION_ORIGIN)

internal actual fun replaceUrl(url: String): Unit = js(JS_REPLACE_STATE)

internal actual fun storageGet(key: String): String = js(JS_STORAGE_GET)

internal actual fun storageSet(key: String, value: String): Unit = js(JS_STORAGE_SET)

internal actual fun storageRemove(key: String): Unit = js(JS_STORAGE_REMOVE)
