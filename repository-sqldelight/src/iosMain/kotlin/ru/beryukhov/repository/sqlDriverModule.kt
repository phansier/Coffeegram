package ru.beryukhov.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.koin.dsl.module

private fun createDriver(): SqlDriver {
    return NativeSqliteDriver(CoffeeDb.Schema, "test.db")
}

actual fun sqlDriverModule() = module {
    single<SqlDriver> { createDriver() }
}
