package repository.model

import io.realm.kotlin.types.RealmObject

internal class RealmDayCoffee : RealmObject {
    var date: String = ""
    var coffeeName: String = ""
    var count: Int = 0
}

data class DbDayCoffee(val date: String, val coffeeName: String, val count: Int)

// internal fun DbDayCoffee.toDayCoffee

internal fun DbDayCoffee.toRealm(): RealmDayCoffee {
    val dc = this
    return RealmDayCoffee().apply {
        date = dc.date
        coffeeName = dc.coffeeName
        count = dc.count
    }
}

internal fun RealmDayCoffee.toDb() = DbDayCoffee(date = date, coffeeName = coffeeName, count = count)
