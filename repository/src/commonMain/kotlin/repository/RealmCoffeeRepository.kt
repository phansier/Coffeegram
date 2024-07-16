package repository

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import repository.model.DbDayCoffee
import repository.model.RealmDayCoffee
import repository.model.toDb
import repository.model.toRealm
import repository.room.AppDatabase
import repository.room.DayCoffee
import repository.room.DayCoffeeDao

interface CoffeeRepository {
    suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>)
    suspend fun getAll(): List<DbDayCoffee>
}

class RealmCoffeeRepository : CoffeeRepository {
    private val realm: Realm by lazy {
        val configuration = RealmConfiguration.Builder(schema = setOf(RealmDayCoffee::class)).build()
        Realm.open(configuration)
    }

    // blocking
    override suspend fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        val all = realm.query<RealmDayCoffee>().find()
        if (all.isEmpty()) {
            create(dbDateCoffees)
        } else {
            update(dbDateCoffees, all)
        }
    }

    private fun create(dateCoffees: List<DbDayCoffee>) {
        dateCoffees.forEach {
            realm.writeBlocking {
                this.copyToRealm(it.toRealm())
            }
        }
    }

    private fun update(
        newCoffees: List<DbDayCoffee>,
        oldCoffees: List<RealmDayCoffee>
    ) {
        newCoffees.forEach { rdc ->
            oldCoffees.firstOrNull { it.date == rdc.date && it.coffeeName == rdc.coffeeName && it.count != rdc.count }
                ?.also { old ->
                    // found RDC to update count
                    realm.writeBlocking {
                        findLatest(old)!!.count = rdc.count
                    }
                } ?: run {
                // coffee with this date and name was not found -> needs to be written
                realm.writeBlocking {
                    this.copyToRealm(rdc.toRealm())
                }
            }
        }
    }

    override suspend fun getAll(): List<DbDayCoffee> {
        return realm.query<RealmDayCoffee>().find().map { it.toDb() }
    }
}

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
