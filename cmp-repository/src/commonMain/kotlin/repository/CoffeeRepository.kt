package repository

import repository.model.DbDayCoffee

interface CoffeeRepository {
    suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>)
    suspend fun getAll(): List<DbDayCoffee>
}

class InMemoryCoffeeRepository : CoffeeRepository {
    private val db = mutableListOf<DbDayCoffee>()

    override suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        db.removeAll { dbDateCoffees.contains(it) }
        db.addAll(dbDateCoffees)
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return db
    }
}
