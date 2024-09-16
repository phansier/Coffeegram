package repository

import repository.model.DbDayCoffee

interface CoffeeRepository {
    fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>)
    fun getAll(): List<DbDayCoffee>
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
