package ru.beryukhov.coffeegram

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.defaultComponentContext
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ru.beryukhov.coffeegram.animations.TransitionSlot
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.WEAR_CAPABILITY
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationConstants.NAVIGATION_STATE_KEY
import ru.beryukhov.coffeegram.model.NavigationConstants.TODAYS_COFFEE_LIST
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.wearable.WearableSyncService

class MainActivity : ComponentActivity() {

    private val wearableSyncService by lazy { WearableSyncService(this) }
    private val daysCoffeesStore: DaysCoffeesStore by inject()
    private val wearableActivityStarter = MutableStateFlow<(() -> Unit)?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)

        val themeStore: ThemeStore = get()

        observeTodaysCoffeeForWear(daysCoffeesStore)

        val rootComponent = DefaultRootComponent(
            context = defaultComponentContext(),
            themeStore = themeStore,
            daysCoffeesStore = daysCoffeesStore,
            showMap = true,
            onAndroidStartWearableActivity = wearableActivityStarter,
            onAndroidIconChange = { isSummer -> changeIcon(this, isSummer) },
        )

        setContent {
            var doAnimationState by rememberSaveable {
                mutableStateOf(true)
            }
            TransitionSlot(
                doAnimation = doAnimationState,
                StartPage = { modifier -> LandingPage(modifier = modifier) },
                EndPage = { modifier, _ ->
                    RootScreen(
                        rootComponent = rootComponent,
                        modifier = modifier,
                    )
                },
            ) {
                doAnimationState = false
                handleDeepLink(rootComponent)
            }
        }
    }

    private fun handleDeepLink(rootComponent: DefaultRootComponent) {
        if (intent.getStringExtra(NAVIGATION_STATE_KEY) == TODAYS_COFFEE_LIST) {
            // Navigate to coffee list for today
            // The CoffeeEditComponent handles the navigation to DayList internally
            // We just need to ensure we're on the CoffeeEdit tab (index 0)
            rootComponent.selectPage(0)
        }
    }

    private fun observeTodaysCoffeeForWear(daysCoffeesStore: DaysCoffeesStore) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val paired = hasPairedWearable()
                wearableActivityStarter.value = if (paired) ::startWearableActivity else null
                if (!paired) return@repeatOnLifecycle

                daysCoffeesStore.state
                    .map { state ->
                        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        state.coffees[today] ?: DayCoffee()
                    }
                    .distinctUntilChanged()
                    .collect { dayCoffee ->
                        wearableSyncService.sendDayCoffee(lifecycleScope, dayCoffee)
                    }
            }
        }
    }

    private suspend fun hasPairedWearable(): Boolean = try {
        Wearable.getCapabilityClient(this)
            .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_ALL)
            .await()
            .nodes.isNotEmpty()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Log.d("MainActivity", "capability lookup failed: $exception")
        false
    }

    private fun startWearableActivity() {
        wearableSyncService.startWearableActivity(lifecycleScope)
    }
}
