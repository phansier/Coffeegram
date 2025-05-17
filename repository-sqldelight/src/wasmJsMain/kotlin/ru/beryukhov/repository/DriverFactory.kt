package ru.beryukhov.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import org.koin.dsl.module

private fun createDriver(): SqlDriver = createDefaultWebWorkerDriver()

actual fun sqlDriverModule() = module {
    single<SqlDriver> { createDriver() }
}
