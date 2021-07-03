package ru.beryukhov.repository

import io.realm.Cancellable
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.beryukhov.repository.model.DateCoffees
import ru.beryukhov.repository.model.DayCoffee

class CoffeeRepository {
    private val realm: Realm by lazy {
        val configuration = RealmConfiguration(schema = setOf(DateCoffees::class, DayCoffee::class))
        Realm.open(configuration)
    }

    // blocking
    fun createOrUpdate(dateCoffees: List<DateCoffees>) {
        val all = realm.objects<DateCoffees>()
        if (all.isEmpty()) {
            create(dateCoffees)
        } else {
            update(dateCoffees, all)
        }
    }

    private fun create(dateCoffees: List<DateCoffees>) {
        dateCoffees.forEach {
            realm.writeBlocking {
                this.copyToRealm(it)
            }
        }
    }

    private fun update(newCoffees: List<DateCoffees>, oldCoffees: List<DateCoffees>) {
        val oldSet = oldCoffees.toSet()

        newCoffees.forEach {
            if (!oldSet.contains(it)) {
                //new date or updated
                realm.objects<DateCoffees>().query("date == ${it.date} LIMIT(1)")
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
        val cancellable: Cancellable = realm.objects<DateCoffees>().observe { result ->
            this.trySend(result.toList()).isSuccess
        }

        awaitClose {
            cancellable.cancel()
        }
    }
}