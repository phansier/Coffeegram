package repository

import repository.model.DbDayCoffee
import repository.room.AppDatabase
import repository.room.DayCoffee
import repository.room.DayCoffeeDao

class RoomCoffeeRepository(private val database: AppDatabase) : CoffeeRepository {

    private val dao: DayCoffeeDao by lazy {
        database.getDao()
    }

    override suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        val savedCounts = dao.getAll().associate { coffeeDay(it.date, it.coffeeName) to it.count }
        dao.insertOrReplace(
            dbDateCoffees
                .filter { savedCounts[coffeeDay(it.date, it.coffeeName)] != it.count }
                .map { DayCoffee(date = it.date, coffeeName = it.coffeeName, count = it.count) }
        )
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return dao.getAll().map { DbDayCoffee(date = it.date, coffeeName = it.coffeeName, count = it.count) }
    }
}

private fun coffeeDay(date: String, coffeeName: String): Pair<String, String> = date to coffeeName
