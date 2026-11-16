package repository

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.runBlocking
import repository.model.DbDayCoffee
import repository.room.AppDatabase
import repository.room.DayCoffee
import repository.room.MIGRATION_1_2
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppDatabaseMigrationTest {

    private val databaseFile = File.createTempFile("coffee-migration", ".db").apply { delete() }
    private val driver = BundledSQLiteDriver()

    @AfterTest
    fun deleteDatabase() {
        databaseFile.parentFile.listFiles { file -> file.name.startsWith(databaseFile.name) }?.forEach(File::delete)
    }

    private fun createVersion1Database(vararg rows: Triple<String, String, Int>) {
        driver.open(databaseFile.path).use { connection ->
            connection.execSQL(
                "CREATE TABLE IF NOT EXISTS `DayCoffee` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`date` TEXT NOT NULL, `coffeeName` TEXT NOT NULL, `count` INTEGER NOT NULL)"
            )
            rows.forEach { (date, coffeeName, count) ->
                connection.execSQL(
                    "INSERT INTO DayCoffee(date, coffeeName, count) VALUES ('$date', '$coffeeName', $count)"
                )
            }
            connection.execSQL("PRAGMA user_version = 1")
        }
    }

    private fun openMigratedDatabase(): AppDatabase =
        Room.databaseBuilder<AppDatabase>(name = databaseFile.path).addMigrations(MIGRATION_1_2).setDriver(driver)
            .build()

    @Test
    fun migrationKeepsLatestCountOfEachDuplicateGroup() = runBlocking {
        createVersion1Database(
            Triple("2026-11-16", "Latte", 11),
            Triple("2026-11-16", "Latte", 2),
            Triple("2026-11-16", "Cappuccino", 3),
            Triple("2026-11-16", "Latte", 8),
            Triple("2026-11-17", "Latte", 1),
        )
        val database = openMigratedDatabase()

        assertEquals(
            setOf(
                DbDayCoffee("2026-11-16", "Latte", 11),
                DbDayCoffee("2026-11-16", "Cappuccino", 3),
                DbDayCoffee("2026-11-17", "Latte", 1),
            ),
            RoomCoffeeRepository(database).getAll().toSet(),
        )
        database.close()
    }

    @Test
    fun migratedDatabaseReplacesInsteadOfDuplicating() = runBlocking {
        createVersion1Database(Triple("2026-11-16", "Latte", 1))
        val database = openMigratedDatabase()

        database.getDao().insertOrReplace(listOf(DayCoffee(date = "2026-11-16", coffeeName = "Latte", count = 5)))

        assertEquals(listOf(5), database.getDao().getAll().map { it.count })
        database.close()
    }
}
