package ru.beryukhov.coffeegram

import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.model.ThemeStorage
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.coffeegram.repository.datastoreModule
import ru.beryukhov.coffeegram.repository.themeDataStorePrefStorage
import ru.beryukhov.repository.databaseModule

val dataStoreModule = module {
    includes(datastoreModule())

    single<ThemeStorage> {
        themeDataStorePrefStorage()
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
