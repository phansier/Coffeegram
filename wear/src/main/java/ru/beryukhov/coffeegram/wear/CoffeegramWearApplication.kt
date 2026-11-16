package ru.beryukhov.coffeegram.wear

import android.app.Application
import android.content.Context
import com.google.android.gms.wearable.Wearable

class CoffeegramWearApplication : Application() {
    val coffeeRepository: WearCoffeeRepository by lazy { WearCoffeeRepository(Wearable.getDataClient(this)) }
}

val Context.coffeeRepository: WearCoffeeRepository
    get() = (applicationContext as CoffeegramWearApplication).coffeeRepository
