package repository.room

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.dsl.module
import java.io.File

actual fun roomDriverModule() = module {
    single<AppDatabase> { getDatabase() }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
        .addMigrations(MIGRATION_1_2)
        .setDriver(BundledSQLiteDriver())
}

fun getDatabase(): AppDatabase {
    return getDatabaseBuilder().build()
}
