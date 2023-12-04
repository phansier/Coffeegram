package ru.beryukhov.coffeegram.data

fun coffeeTypeValues() = listOf(Latte, Cappuccino, Americano, CommonCoffee) // todo replace it by some enum
fun coffeeTypeValueOf(name: String): CoffeeType {
    return when (name) {
        Latte.name -> Latte
        Cappuccino.name -> Cappuccino
        Americano.name -> Americano
        else -> CommonCoffee
    }
}

abstract class CoffeeType constructor(
    val name: String,
    val iconRes: String = Res.drawable.coffee
)

object Cappuccino : CoffeeType(Res.string.cappucino, Res.drawable.cappucino)
object Latte : CoffeeType(Res.string.latte, Res.drawable.latte)
object Americano : CoffeeType(Res.string.americano, Res.drawable.coffee)
object CommonCoffee : CoffeeType(Res.string.common)

object Res {
    object drawable {

        const val cappucino = "drawable/cappucino.xml"
        const val latte = "drawable/latte.xml"
        const val coffee = "drawable/coffee.xml"
    }

    object string {
        const val cappucino = "Cappuccino"
        const val latte = "Latte"
        const val americano = "Americano"
        const val common = "CommonCoffee"
    }
}
