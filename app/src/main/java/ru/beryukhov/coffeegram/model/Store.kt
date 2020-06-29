package ru.beryukhov.coffeegram.model


interface Store<in Intent : Any, out State : Any>// : SendChannel<Intent>, StateFlow<State>

