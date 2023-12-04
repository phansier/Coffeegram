package ru.beryukhov.coffeegram

internal operator fun <E> List<E>.times(i: Int): List<E> {
    val result = mutableListOf<E>()
    repeat(i) {
        result.addAll(this)
    }
    return result
}
