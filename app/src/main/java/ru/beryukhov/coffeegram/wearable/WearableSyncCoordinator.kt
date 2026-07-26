package ru.beryukhov.coffeegram.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.WEAR_CAPABILITY
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import kotlin.time.Clock

private const val TAG = "WearSyncCoordinator"

class WearableSyncCoordinator(
    private val context: Context,
    daysCoffeesStoreProvider: () -> DaysCoffeesStore,
    wearableSyncServiceProvider: () -> WearableSyncService = { WearableSyncService(context) },
) {
    private val daysCoffeesStore by lazy(daysCoffeesStoreProvider)
    private val wearableSyncService by lazy(wearableSyncServiceProvider)
    private val scope = MainScope()

    val wearableActivityStarter: StateFlow<(() -> Unit)?>
        field = MutableStateFlow(null)

    fun start() {
        scope.launch {
            if (!hasPairedWearable()) return@launch
            wearableActivityStarter.value = ::triggerWearableActivity

            daysCoffeesStore.state
                .map { state ->
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    state.coffees[today] ?: DayCoffee()
                }
                .distinctUntilChanged()
                .collect { dayCoffee ->
                    wearableSyncService.sendDayCoffee(scope, dayCoffee)
                }
        }
    }

    private fun triggerWearableActivity() {
        wearableSyncService.startWearableActivity(scope)
    }

    private suspend fun hasPairedWearable(): Boolean = try {
        Wearable.getCapabilityClient(context)
            .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_ALL)
            .await()
            .nodes.isNotEmpty()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Log.d(TAG, "capability lookup failed: $exception")
        false
    }
}
