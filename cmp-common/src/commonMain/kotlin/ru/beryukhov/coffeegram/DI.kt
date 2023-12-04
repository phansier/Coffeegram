package ru.beryukhov.coffeegram

import org.koin.dsl.module
import repository.CoffeeRepository
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStorePrefStorage
import ru.beryukhov.coffeegram.repository.createDataStore
import ru.beryukhov.coffeegram.store_lib.Storage

fun appModule() = module {
    single {
        createDataStore()
    }
    single<Storage<ThemeState>> {
        ThemeDataStorePrefStorage(dataStore = get())
    }
    single {
        ThemeStore(get())
    }
    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
    single { CoffeeRepository() }
    single { CoffeeStorage(repository = get()) }

    single { NavigationStore() }
    single { Dependencies(get(), get(), get()) }
}
