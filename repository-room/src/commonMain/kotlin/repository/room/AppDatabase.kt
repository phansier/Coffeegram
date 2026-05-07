package repository.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import org.koin.core.module.Module

@Database(entities = [DayCoffee::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DayCoffeeDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

expect fun roomDriverModule(): Module
