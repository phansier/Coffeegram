package ru.beryukhov.coffeegram.wear

import ru.beryukhov.coffeegram.data.DayCoffee

val DayCoffee.total: Int
    get() = coffeeCountMap.values.sum()
