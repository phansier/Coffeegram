package repository

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.query
import repository.model.DbDayCoffee
import repository.model.RealmDayCoffee
import repository.model.toDb
import repository.model.toRealm

class CoffeeRepository {
    private val realm: Realm by lazy {
        val configuration = RealmConfiguration.with(schema = setOf(RealmDayCoffee::class))
        Realm.open(configuration)
    }

    // blocking
    fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
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
                }
            oldCoffees.firstOrNull { it.date == rdc.date && it.coffeeName == rdc.coffeeName }
                ?: run {
                    // coffee with this date and name was not found -> needs to be written
                    realm.writeBlocking {
                        this.copyToRealm(rdc.toRealm())
                    }
                }
        }
    }

    fun getAll(): List<DbDayCoffee> {
        return realm.query<RealmDayCoffee>().find().map { it.toDb() }
    }
}
