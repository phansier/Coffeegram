@file:Suppress("MatchingDeclarationName")

package ru.beryukhov.coffeegram.repository

actual object Log {
    actual fun d(tag: String, message: String) {
        println("$tag: $message")
    }
}
