package ru.beryukhov.repository

import org.koin.core.module.Module
import org.koin.dsl.module
import repository.CoffeeRepository
import repository.SqldCoffeeRepository

expect fun sqlDriverModule(): Module

val databaseModule = module {
    includes(sqlDriverModule())

    single<SqlDayCoffeeQueries> {
        val database = CoffeeDb(get())
        database.sqlDayCoffeeQueries
    }
    single<CoffeeRepository> { SqldCoffeeRepository(get()) }
}
