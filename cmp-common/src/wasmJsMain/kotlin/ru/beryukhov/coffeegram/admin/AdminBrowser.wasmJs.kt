@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@file:Suppress("MatchingDeclarationName")

package ru.beryukhov.coffeegram.admin

internal actual fun locationSearch(): String = js(JS_LOCATION_SEARCH)
