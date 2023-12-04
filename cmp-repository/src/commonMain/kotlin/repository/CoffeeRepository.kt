package repository

import repository.model.DbDayCoffee
import repository.model.toDb
import ru.beryukhov.repository.DriverFactory
import ru.beryukhov.repository.SqlDayCoffee
import ru.beryukhov.repository.SqlDayCoffeeQueries
import ru.beryukhov.repository.createDatabase

class CoffeeRepository {
    private val db: SqlDayCoffeeQueries by lazy {
        val driverFactory = DriverFactory()
        val db = createDatabase(driverFactory).sqlDayCoffeeQueries
        db.createSqlDayCoffeeTable()
        return@lazy db
    }

    // blocking
    fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
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

    fun getAll(): List<DbDayCoffee> {
        return db.selectAll().executeAsList().map { it.toDb() }
    }
}
