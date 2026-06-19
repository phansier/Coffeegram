package ru.beryukhov.coffeegram.repository

expect object Log {
    fun d(tag: String, message: String)
}
