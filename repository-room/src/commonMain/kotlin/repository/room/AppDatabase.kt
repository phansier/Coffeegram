package repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.koin.core.module.Module

@Database(entities = [DayCoffee::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DayCoffeeDao
}

expect fun roomDriverModule(): Module
