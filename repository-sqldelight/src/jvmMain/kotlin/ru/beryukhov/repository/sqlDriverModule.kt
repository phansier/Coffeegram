package ru.beryukhov.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.koin.dsl.module

private fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:myDatabase.db")
    CoffeeDb.Schema.create(driver)
    return driver
}

actual fun sqlDriverModule() = module {
    single<SqlDriver> { createDriver() }
}
