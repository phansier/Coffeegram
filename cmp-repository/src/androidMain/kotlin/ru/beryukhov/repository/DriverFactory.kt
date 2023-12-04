package ru.beryukhov.repository

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

lateinit var context: Context // todo fill it

internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(CoffeeDb.Schema, context, "test.db")
    }
}
