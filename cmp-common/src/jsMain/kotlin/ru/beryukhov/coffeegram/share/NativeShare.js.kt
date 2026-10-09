package ru.beryukhov.coffeegram.share

internal actual fun jsCanShare(): Boolean = js(JS_CAN_SHARE) as Boolean

internal actual fun jsShareText(text: String) {
    js(JS_SHARE_TEXT)
}
