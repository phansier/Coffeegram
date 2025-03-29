package repository

import repository.model.DbDayCoffee
import repository.model.toDb
import ru.beryukhov.repository.SqlDayCoffee
import ru.beryukhov.repository.SqlDayCoffeeQueries

internal class SqldCoffeeRepository(private val db: SqlDayCoffeeQueries) : CoffeeRepository {

    init {
        db.createSqlDayCoffeeTable()
    }

    override suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        val all = db.selectAll().executeAsList()
        if (all.isEmpty()) {
            create(dbDateCoffees)
        } else {
            update(dbDateCoffees, all)
        }
    }

    private fun create(dateCoffees: List<DbDayCoffee>) {
        dateCoffees.forEach {
            db.insertSqlDayCoffee(it.date, it.coffeeName, it.count.toLong())
        }
    }

    private fun update(
        newCoffees: List<DbDayCoffee>,
        oldCoffees: List<SqlDayCoffee>
    ) {
        newCoffees.forEach { rdc ->
            oldCoffees.firstOrNull {
                it.date == rdc.date &&
                    it.coffeeName == rdc.coffeeName &&
                    it.count.toInt() != rdc.count
            }
                ?.also { old ->
                    // found RDC to update count
                    db.updateSqlDayCoffee(old.date, old.coffeeName, rdc.count.toLong())
                }
            oldCoffees.firstOrNull { it.date == rdc.date && it.coffeeName == rdc.coffeeName }
                ?: run {
                    // coffee with this date and name was not found -> needs to be written
                    db.insertSqlDayCoffee(rdc.date, rdc.coffeeName, rdc.count.toLong())
                }
        }
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return db.selectAll().executeAsList().map { it.toDb() }
    }
}
