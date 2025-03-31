@file:OptIn(ExperimentalResourceApi::class)

package ru.beryukhov.coffeegram.data

import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.americano
import coffeegram.cmp_common.generated.resources.cappuccino
import coffeegram.cmp_common.generated.resources.chocolate
import coffeegram.cmp_common.generated.resources.cocoa
import coffeegram.cmp_common.generated.resources.coffee
import coffeegram.cmp_common.generated.resources.espresso
import coffeegram.cmp_common.generated.resources.frappe
import coffeegram.cmp_common.generated.resources.fredo
import coffeegram.cmp_common.generated.resources.glace
import coffeegram.cmp_common.generated.resources.irish
import coffeegram.cmp_common.generated.resources.latte
import coffeegram.cmp_common.generated.resources.macchiato
import coffeegram.cmp_common.generated.resources.mocha
import org.jetbrains.compose.resources.ExperimentalResourceApi

interface CoffeeType {
    val localizedName: PrintableText
    val icon: Picture
    val dbKey: String // non-localizable name for data storage
}

enum class CoffeeTypes(
    override val localizedName: PrintableText.Res,
    override val icon: Picture = Image(Res.drawable.coffee),

    ) : CoffeeType {
    Cappuccino(PrintableText.Res(Res.string.cappuccino), Image(Res.drawable.cappuccino)),
    Latte(PrintableText.Res(Res.string.latte), Image(Res.drawable.latte)),
    Americano(PrintableText.Res(Res.string.americano), Image(Res.drawable.americano)),
    Macchiato(PrintableText.Res(Res.string.macchiato), Image(Res.drawable.macchiato)),
    Glace(PrintableText.Res(Res.string.glace), Image(Res.drawable.glace)),
    Frappe(PrintableText.Res(Res.string.frappe), Image(Res.drawable.latte)),
    Espresso(PrintableText.Res(Res.string.espresso), Image(Res.drawable.espresso)),
    Mocha(PrintableText.Res(Res.string.mocha), Image(Res.drawable.mocha)),
    Fredo(PrintableText.Res(Res.string.fredo), Image(Res.drawable.fredo)),
    Irish(PrintableText.Res(Res.string.irish), Image(Res.drawable.irish)),
    Cocoa(PrintableText.Res(Res.string.cocoa), Image(Res.drawable.cocoa)),
    Chocolate(PrintableText.Res(Res.string.chocolate), Image(Res.drawable.chocolate)), ;
    // icons from here: https://www.freepik.com/free-vector/list-different-types-coffee_951047.htm
    // app logo is here: https://www.flaticon.com/free-icon/coffee-cup_766408

    override val dbKey: String = this.name
}

data class CoffeeTypeWithCount(
    val coffee: CoffeeType,
    val count: Int
)
