package ru.beryukhov.repository

import io.realm.Cancellable
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.beryukhov.repository.model.DateCoffees
import ru.beryukhov.repository.model.RealmDateCoffees
import ru.beryukhov.repository.model.RealmDayCoffee
import ru.beryukhov.repository.model.toDb
import ru.beryukhov.repository.model.toRealm

class CoffeeRepository {
    private val realm: Realm by lazy {
        val configuration = RealmConfiguration(schema = setOf(RealmDateCoffees::class, RealmDayCoffee::class))
        Realm.open(configuration)
    }

    // blocking
    fun createOrUpdate(dateCoffees: List<DateCoffees>) {
        val all = realm.objects<RealmDateCoffees>()
        val realmDateCoffees = dateCoffees.map{it.toRealm()}
        if (all.isEmpty()) {
            create(realmDateCoffees)
        } else {
            update(realmDateCoffees, all)
        }
    }

    private fun create(dateCoffees: List<RealmDateCoffees>) {
        dateCoffees.forEach {
            realm.writeBlocking {
                this.copyToRealm(it)
            }
        }
    }

    private fun update(newCoffees: List<RealmDateCoffees>, oldCoffees: List<RealmDateCoffees>) {
        val oldSet = oldCoffees.toSet()

        newCoffees.forEach {
            if (!oldSet.contains(it)) {
                //new date or updated
                realm.objects<RealmDateCoffees>().query("date == ${it.date} LIMIT(1)")
                    .firstOrNull()
                    ?.also {
                        //updated
                            updatedDateCoffees ->realm.writeBlocking {
                            updatedDateCoffees.dayCoffees = it.dayCoffees
                        }
                    }
                    ?: run {
                        //new
                        realm.writeBlocking {
                            this.copyToRealm(it)
                        }
                    }
            }
        }
    }


    fun observeChanges(): Flow<List<DateCoffees>> = callbackFlow {
        val cancellable: Cancellable = realm.objects<RealmDateCoffees>().observe { result ->
            this.trySend(result.toList().map{it.toDb()}).isSuccess
        }

        awaitClose {
            cancellable.cancel()
        }
    }

    fun getAll(): List<DateCoffees> {
        return realm.objects<RealmDateCoffees>().map{it.toDb()}
    }
}