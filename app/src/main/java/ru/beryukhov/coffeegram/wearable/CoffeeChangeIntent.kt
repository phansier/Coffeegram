package ru.beryukhov.coffeegram.wearable

import ru.beryukhov.coffeegram.data.CoffeeChangeEvent
import ru.beryukhov.coffeegram.model.DaysCoffeesIntent

fun CoffeeChangeEvent.toDaysCoffeesIntent(): DaysCoffeesIntent =
    if (delta > 0) {
        DaysCoffeesIntent.PlusCoffee(date, type)
    } else {
        DaysCoffeesIntent.MinusCoffee(date, type)
    }
