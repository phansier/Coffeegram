package repository.room

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.koin.dsl.module

actual fun roomDriverModule() = module {
    single<AppDatabase> { getDatabase() }
}
fun getDatabase(): AppDatabase {
    return getDatabaseBuilder().build()
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.databaseBuilder<AppDatabase>(
        name = "my_room.db",
    ).setDriver(createSQLiteWasmWorker())
}

expect fun createSQLiteWasmWorker(): WebWorkerSQLiteDriver
