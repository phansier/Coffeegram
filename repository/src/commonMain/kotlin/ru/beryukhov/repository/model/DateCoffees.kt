package ru.beryukhov.repository.model

import io.realm.RealmObject
import kotlin.jvm.JvmOverloads

internal class RealmDateCoffees : RealmObject {
    /*constructor()
    constructor(date: String, dayCoffees: List<RealmDayCoffee>) {
        this.date = date
        this.dayCoffees = dayCoffees
    }*/

    var date: String = ""
    var dayCoffees: List<RealmDayCoffee> = emptyList()
}

data class DateCoffees(val date: String, val dayCoffees: List<DbDayCoffee>)

internal fun DateCoffees.toRealm(): RealmDateCoffees {
    val dc = this
    return RealmDateCoffees().apply {
        date = dc.date
        dayCoffees = dc.dayCoffees.map { it.toRealm() }
    }
}

internal fun RealmDateCoffees.toDb() =
    DateCoffees(date = date,
        dayCoffees = dayCoffees.map { it.toDb() }
    )


