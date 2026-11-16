package ru.beryukhov.coffeegram.wearable

import android.content.Context
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.beryukhov.coffeegram.data.DatedDayCoffee
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.currentDateFlow
import ru.beryukhov.coffeegram.model.DaysCoffeesStore

class WearableSyncCoordinator(
    context: Context,
    daysCoffeesStoreProvider: () -> DaysCoffeesStore,
    wearableSyncServiceProvider: () -> WearableSyncService = { WearableSyncService(context) },
    private val watchAppInstalled: () -> Flow<Boolean> = { watchAppInstalledFlow(context) },
) {
    private val daysCoffeesStore by lazy(daysCoffeesStoreProvider)
    private val wearableSyncService by lazy(wearableSyncServiceProvider)
    private val scope = MainScope()

    val wearableActivityStarter: StateFlow<(() -> Unit)?>
        field = MutableStateFlow(null)

    fun start() {
        scope.launch {
            watchAppInstalled().collectLatest { installed ->
                wearableActivityStarter.value = if (installed) ::triggerWearableActivity else null
                if (installed) syncTodayToWatch()
            }
        }
    }

    private suspend fun syncTodayToWatch() {
        combine(daysCoffeesStore.state, currentDateFlow()) { state, today ->
            DatedDayCoffee(date = today, dayCoffee = state.coffees[today] ?: DayCoffee())
        }
            .distinctUntilChanged()
            .collect { dayCoffee ->
                wearableSyncService.sendDayCoffee(scope, dayCoffee)
            }
    }

    private fun triggerWearableActivity() {
        wearableSyncService.startWearableActivity(scope)
    }
}
