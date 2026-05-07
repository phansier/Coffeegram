package ru.beryukhov.coffeegram

import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.ThemeDataStorePrefStorage
import ru.beryukhov.coffeegram.repository.datastoreModule
import ru.beryukhov.coffeegram.store_lib.Storage
import ru.beryukhov.repository.databaseModule

val dataStoreModule = module {
    includes(datastoreModule())

    single<Storage<ThemeState>> {
        ThemeDataStorePrefStorage(dataStore = get())
    }
    single {
        ThemeStore(get())
    }
}

val coffeeStorageModule = module {
    includes(databaseModule)
    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
    single { CoffeeStorage(repository = get()) }
}
