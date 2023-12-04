@file:Suppress("MatchingDeclarationName")

package repository.model

import ru.beryukhov.repository.SqlDayCoffee

data class DbDayCoffee(val date: String, val coffeeName: String, val count: Int)

internal fun DbDayCoffee.toRealm(): SqlDayCoffee {
    val dc = this
    return SqlDayCoffee(
        date = dc.date,
        coffeeName = dc.coffeeName,
        count = dc.count.toLong(),
    )
}

internal fun SqlDayCoffee.toDb() = DbDayCoffee(date = date, coffeeName = coffeeName, count = count.toInt())
