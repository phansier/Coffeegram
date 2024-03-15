package repository

import repository.model.DbDayCoffee
import repository.model.toDb
import ru.beryukhov.repository.SqlDayCoffee
import ru.beryukhov.repository.SqlDayCoffeeQueries

interface CoffeeRepository {
    fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>)
    fun getAll(): List<DbDayCoffee>
}

class SqlCoffeeRepositoryImpl: CoffeeRepository {
    private val db: SqlDayCoffeeQueries by lazy {
        val driverFactory = DriverFactory()
        val db = createDatabase(driverFactory).sqlDayCoffeeQueries
        db.createSqlDayCoffeeTable()
    }

    // blocking
    override fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
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

    override fun getAll(): List<DbDayCoffee> {
        return db.selectAll().executeAsList().map { it.toDb() }
    }
}

class InMemoryCoffeeRepository : CoffeeRepository {
    private val db = mutableListOf<DbDayCoffee>()

    override fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        db.removeAll { dbDateCoffees.contains(it) }
        db.addAll(dbDateCoffees)
    }

    override fun getAll(): List<DbDayCoffee> {
        return db
    }
}
