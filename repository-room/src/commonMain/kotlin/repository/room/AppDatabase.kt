package repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.CoffeeRepository
import repository.RoomCoffeeRepository

@Database(entities = [DayCoffee::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DayCoffeeDao
}

expect fun roomDriverModule(): Module
