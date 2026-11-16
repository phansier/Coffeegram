package ru.beryukhov.coffeegram.wear

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class CoffeegramWearApplication : Application() {
    val coffeeRepository: WearCoffeeRepository by lazy { WearCoffeeRepository(Wearable.getDataClient(this)) }

    override fun onCreate() {
        super.onCreate()
        refreshSurfacesWhenCountsChange()
    }

    private fun refreshSurfacesWhenCountsChange() {
        val complicationUpdater = ComplicationDataSourceUpdateRequester.create(
            this,
            ComponentName(this, TodayComplicationService::class.java),
        )
        MainScope().launch {
            coffeeRepository.todayCoffee.drop(1).collect {
                TileService.getUpdater(this@CoffeegramWearApplication).requestUpdate(TodayTileService::class.java)
                complicationUpdater.requestUpdateAll()
            }
        }
    }
}

val Context.coffeeRepository: WearCoffeeRepository
    get() = (applicationContext as CoffeegramWearApplication).coffeeRepository
