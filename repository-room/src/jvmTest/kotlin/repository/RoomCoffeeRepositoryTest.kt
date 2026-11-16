package repository

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.runBlocking
import repository.model.DbDayCoffee
import repository.room.AppDatabase
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomCoffeeRepositoryTest {

    private val database = Room.inMemoryDatabaseBuilder<AppDatabase>()
        .setDriver(BundledSQLiteDriver())
        .build()
    private val dao = database.getDao()
    private val repository = RoomCoffeeRepository(database)

    private fun latte(count: Int) = DbDayCoffee(date = "2026-11-16", coffeeName = "Latte", count = count)
    private fun cappuccino(count: Int) = DbDayCoffee(date = "2026-11-16", coffeeName = "Cappuccino", count = count)

    @AfterTest
    fun closeDatabase() = database.close()

    @Test
    fun savingUnchangedCountsDoesNotAddRows() = runBlocking {
        repeat(3) { repository.createOrUpdate(listOf(latte(1), cappuccino(2))) }
        assertEquals(2, dao.getAll().size)
    }

    @Test
    fun latestCountSurvivesReloadAfterRepeatedSaves() = runBlocking {
        repository.createOrUpdate(listOf(latte(1), cappuccino(2)))
        repository.createOrUpdate(listOf(latte(1), cappuccino(3)))
        repository.createOrUpdate(listOf(latte(2), cappuccino(3)))
        assertEquals(listOf(latte(2), cappuccino(3)), repository.getAll().sortedBy { it.coffeeName }.reversed())
    }
}
