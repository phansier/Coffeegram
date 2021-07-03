package ru.beryukhov.repository

import io.realm.Cancellable
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.beryukhov.repository.model.DbDayCoffee
import ru.beryukhov.repository.model.RealmDayCoffee
import ru.beryukhov.repository.model.toDb
import ru.beryukhov.repository.model.toRealm

class CoffeeRepository {
    private val realm: Realm by lazy {
        val configuration = RealmConfiguration(schema = setOf(RealmDayCoffee::class))
        Realm.open(configuration)
    }

    // blocking
    fun createOrUpdate(dbDateCoffees: List<DbDayCoffee>) {
        val all = realm.objects<RealmDayCoffee>()
        val realmDateCoffees = dbDateCoffees.map{it.toRealm()}
        if (all.isEmpty()) {
            create(realmDateCoffees)
        } else {
            update(realmDateCoffees, all)
        }
    }

    private fun create(dateCoffees: List<RealmDayCoffee>) {
        dateCoffees.forEach {
            realm.writeBlocking {
                this.copyToRealm(it)
            }
        }
    }

    private fun update(newCoffees: List<RealmDayCoffee>, oldCoffees: List<RealmDayCoffee>) {
        val oldSet = oldCoffees.toSet()

        newCoffees.forEach {
            TODO()
            /*if (!oldSet.find {  }) {
                //new date or updated
                realm.objects<RealmDayCoffee>().query()
                    .firstOrNull()
                    ?.also {
                        //updated
                            updatedDateCoffees ->realm.writeBlocking {
                            updatedDateCoffees. = it.dayCoffees
                        }
                    }
                    ?: run {
                        //new
                        realm.writeBlocking {
                            this.copyToRealm(it)
                        }
                    }
            }*/
        }
    }


    fun observeChanges(): Flow<List<DbDayCoffee>> = callbackFlow {
        val cancellable: Cancellable = realm.objects<RealmDayCoffee>().observe { result ->
            this.trySend(result.toList().map{it.toDb()}).isSuccess
        }

        awaitClose {
            cancellable.cancel()
        }
    }

    fun getAll(): List<DbDayCoffee> {
        return realm.objects<RealmDayCoffee>().map{it.toDb()}
    }
}