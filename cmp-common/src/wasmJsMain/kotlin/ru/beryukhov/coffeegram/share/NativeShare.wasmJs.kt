@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package ru.beryukhov.coffeegram.share

internal actual fun jsCanShare(): Boolean = js(JS_CAN_SHARE)

internal actual fun jsShareText(text: String): Unit = js(JS_SHARE_TEXT)
