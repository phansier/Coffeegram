package repository.model

import ru.beryukhov.repository.SqlDayCoffee

internal fun SqlDayCoffee.toDb() = DbDayCoffee(date = date, coffeeName = coffeeName, count = count.toInt())
