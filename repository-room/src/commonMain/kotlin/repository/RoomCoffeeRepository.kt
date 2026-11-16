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
        val rowsByCoffeeDay = dao.getAll().groupBy { coffeeDay(it.date, it.coffeeName) }
        val newWithRows = dbDateCoffees.map { new -> new to rowsByCoffeeDay[coffeeDay(new.date, new.coffeeName)].orEmpty() }
        dao.insertAll(
            newWithRows
                .filter { (_, rows) -> rows.isEmpty() }
                .map { (new, _) -> DayCoffee(date = new.date, coffeeName = new.coffeeName, count = new.count) }
        )
        dao.updateAll(
            newWithRows.mapNotNull { (new, rows) ->
                rows.firstOrNull()?.takeIf { it.count != new.count }?.copy(count = new.count)
            }
        )
        dao.deleteAll(newWithRows.flatMap { (_, rows) -> rows.drop(1) })
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return dao.getAll()
            .distinctBy { coffeeDay(it.date, it.coffeeName) }
            .map { DbDayCoffee(date = it.date, coffeeName = it.coffeeName, count = it.count) }
    }
}

private fun coffeeDay(date: String, coffeeName: String): Pair<String, String> = date to coffeeName
