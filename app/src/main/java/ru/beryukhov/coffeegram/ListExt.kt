package ru.beryukhov.coffeegram


internal operator fun <E> List<E>.times(i: Int): List<E> {
    val result = mutableListOf<E>()
    for (j in 0 until i) {
        result.addAll(this)
    }
    return result
}