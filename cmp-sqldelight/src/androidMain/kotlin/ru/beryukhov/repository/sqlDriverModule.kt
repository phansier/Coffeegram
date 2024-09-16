package ru.beryukhov.repository

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.dsl.module

private fun createDriver(context: Context): SqlDriver {
    return AndroidSqliteDriver(CoffeeDb.Schema, context, "test.db")
}

actual fun sqlDriverModule() = module {
    single<SqlDriver> { createDriver(get()) }
}
