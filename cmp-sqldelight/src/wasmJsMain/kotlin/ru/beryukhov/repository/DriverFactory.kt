package ru.beryukhov.repository

import app.cash.sqldelight.db.SqlDriver

internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver = WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
            )
        )
        return driver
    }
}
