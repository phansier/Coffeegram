package ru.beryukhov.coffeegram.data

import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.americano
import coffeegram.cmp_common.generated.resources.cappuccino
import coffeegram.cmp_common.generated.resources.chocolate
import coffeegram.cmp_common.generated.resources.cocoa
import coffeegram.cmp_common.generated.resources.espresso
import coffeegram.cmp_common.generated.resources.frappe
import coffeegram.cmp_common.generated.resources.fredo
import coffeegram.cmp_common.generated.resources.glace
import coffeegram.cmp_common.generated.resources.irish
import coffeegram.cmp_common.generated.resources.latte
import coffeegram.cmp_common.generated.resources.macchiato
import coffeegram.cmp_common.generated.resources.mocha
import ru.beryukhov.coffeegram.data.icons.Americano
import ru.beryukhov.coffeegram.data.icons.Cappuccino
import ru.beryukhov.coffeegram.data.icons.Chocolate
import ru.beryukhov.coffeegram.data.icons.Cocoa
import ru.beryukhov.coffeegram.data.icons.Coffee
import ru.beryukhov.coffeegram.data.icons.CoffeeIcons
import ru.beryukhov.coffeegram.data.icons.Espresso
import ru.beryukhov.coffeegram.data.icons.Fredo
import ru.beryukhov.coffeegram.data.icons.Glace
import ru.beryukhov.coffeegram.data.icons.Irish
import ru.beryukhov.coffeegram.data.icons.Latte
import ru.beryukhov.coffeegram.data.icons.Macchiato
import ru.beryukhov.coffeegram.data.icons.Mocha

interface CoffeeType {
    val localizedName: PrintableText
    val icon: Picture
    val dbKey: String // non-localizable name for data storage
}

enum class CoffeeTypes(
    override val localizedName: PrintableText.Res,
    override val icon: Picture = Vector(CoffeeIcons.Coffee),

    ) : CoffeeType {
    Cappuccino(PrintableText.Res(Res.string.cappuccino), Vector(CoffeeIcons.Cappuccino)),
    Latte(PrintableText.Res(Res.string.latte), Vector(CoffeeIcons.Latte)),
    Americano(PrintableText.Res(Res.string.americano), Vector(CoffeeIcons.Americano)),
    Macchiato(PrintableText.Res(Res.string.macchiato), Vector(CoffeeIcons.Macchiato)),
    Glace(PrintableText.Res(Res.string.glace), Vector(CoffeeIcons.Glace)),
    Frappe(PrintableText.Res(Res.string.frappe), Vector(CoffeeIcons.Latte)),
    Espresso(PrintableText.Res(Res.string.espresso), Vector(CoffeeIcons.Espresso)),
    Mocha(PrintableText.Res(Res.string.mocha), Vector(CoffeeIcons.Mocha)),
    Fredo(PrintableText.Res(Res.string.fredo), Vector(CoffeeIcons.Fredo)),
    Irish(PrintableText.Res(Res.string.irish), Vector(CoffeeIcons.Irish)),
    Cocoa(PrintableText.Res(Res.string.cocoa), Vector(CoffeeIcons.Cocoa)),
    Chocolate(PrintableText.Res(Res.string.chocolate), Vector(CoffeeIcons.Chocolate)), ;
    // icons from here: https://www.freepik.com/free-vector/list-different-types-coffee_951047.htm
    // app logo is here: https://www.flaticon.com/free-icon/coffee-cup_766408

    override val dbKey: String = this.name
}

data class CoffeeTypeWithCount(
    val coffee: CoffeeType,
    val count: Int
)
