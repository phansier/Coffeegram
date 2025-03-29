package ru.beryukhov.repository

import org.koin.dsl.module
import repository.CoffeeRepository
import repository.RoomCoffeeRepository
import repository.room.roomDriverModule

val databaseModule = module {
    includes(roomDriverModule())
    single<CoffeeRepository> { RoomCoffeeRepository(get()) }
}
