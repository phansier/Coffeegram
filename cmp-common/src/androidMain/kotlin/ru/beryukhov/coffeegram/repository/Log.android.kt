package ru.beryukhov.coffeegram.repository

actual object Log {
    actual fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }
}
