package ru.beryukhov.repository

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}