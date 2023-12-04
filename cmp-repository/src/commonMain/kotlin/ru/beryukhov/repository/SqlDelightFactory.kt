@file:Suppress("MatchingDeclarationName")

package ru.beryukhov.repository

import app.cash.sqldelight.db.SqlDriver

internal expect class DriverFactory() {
    fun createDriver(): SqlDriver
}

internal fun createDatabase(driverFactory: DriverFactory): CoffeeDb {
    val driver = driverFactory.createDriver()
    val database = CoffeeDb(driver)

    // Do more work with the database (see below).

    return database
}
