package repository.model

import ru.beryukhov.repository.SqlDayCoffee

data class DbDayCoffee(val date: String, val coffeeName: String, val count: Int)

internal fun SqlDayCoffee.toDb() = DbDayCoffee(date = date, coffeeName = coffeeName, count = count.toInt())
