package ru.beryukhov.coffeegram

internal operator fun <E> List<E>.times(i: Int): List<E> {
    val result = mutableListOf<E>()
    (0 until i).forEach {
        result.addAll(this)
    }
    return result
}
