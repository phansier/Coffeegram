package ru.beryukhov.repository.model

import io.realm.RealmObject
import kotlin.jvm.JvmOverloads

internal class RealmDayCoffee : RealmObject {
    /*constructor()
    constructor(coffeeName: String, count: Int) {
        this.coffeeName = coffeeName
        this.count = count
    }*/

    var coffeeName: String = ""
    var count: Int = 0
}

data class DbDayCoffee(val coffeeName: String, val count: Int)

internal fun DbDayCoffee.toRealm(): RealmDayCoffee {
    val dc = this
    return RealmDayCoffee().apply {
        coffeeName = dc.coffeeName
        count = dc.count
    }
}

internal fun RealmDayCoffee.toDb() = DbDayCoffee(coffeeName = coffeeName, count = count)