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
        val all = dao.getAll()
        if (all.isEmpty()) {
            create(dbDateCoffees)
        } else {
            update(dbDateCoffees, all)
        }
    }

    private suspend fun create(dateCoffees: List<DbDayCoffee>) {
        dateCoffees.forEach {
            dao.insert(DayCoffee(date = it.date, coffeeName = it.coffeeName, count = it.count))
        }
    }

    private suspend fun update(
        newCoffees: List<DbDayCoffee>,
        oldCoffees: List<DayCoffee>
    ) {
        newCoffees.forEach { new ->
            oldCoffees.firstOrNull { old ->
                old.date == new.date &&
                    old.coffeeName == new.coffeeName &&
                    old.count != new.count
            }
                ?.also { old ->
                    // found DC to update count
                    dao.update(old.copy(count = new.count))
                } ?: run {
                // coffee with this date and name was not found -> needs to be written
                dao.insert(DayCoffee(date = new.date, coffeeName = new.coffeeName, count = new.count))
            }
        }
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return dao.getAll().map { DbDayCoffee(date = it.date, coffeeName = it.coffeeName, count = it.count) }
    }
}
