package ru.beryukhov.coffeegram

import org.koin.dsl.module
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.DaysCoffeesStoreImpl
import ru.beryukhov.coffeegram.repository.CoffeeStorage
import ru.beryukhov.repository.databaseModule

val coffeeStorageModule = module {
    includes(databaseModule)
    single<DaysCoffeesStore> { DaysCoffeesStoreImpl(coffeeStorage = get()) }
    single { CoffeeStorage(repository = get()) }
}
