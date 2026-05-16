package ru.beryukhov.coffeegram

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.ext.android.get
import ru.beryukhov.coffeegram.animations.TransitionSlot
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.data.CoffeeTypes
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationConstants.NAVIGATION_STATE_KEY
import ru.beryukhov.coffeegram.model.NavigationConstants.TODAYS_COFFEE_LIST
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.wearable.WearableSyncService
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MainActivity : ComponentActivity() {

    private val wearableSyncService by lazy { WearableSyncService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)

        val themeStore: ThemeStore = get()
        val daysCoffeesStore: DaysCoffeesStore = get()
        val showMap = checkCoarseLocationPermission()

        val rootComponent = DefaultRootComponent(
            context = defaultComponentContext(),
            themeStore = themeStore,
            daysCoffeesStore = daysCoffeesStore,
            showMap = showMap,
            onAndroidStartWearableActivity = if (BuildConfig.DEBUG) ::startWearableActivity else null,
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

    private fun startWearableActivity() {
        // Send mock data for now - in production this would use real data from store
        val mockDayCoffee = DayCoffee(
            mapOf(
                CoffeeTypes.Cappuccino to 1,
                CoffeeTypes.Americano to 2
            )
        )
        wearableSyncService.startWearableActivity(lifecycleScope, mockDayCoffee)
    }

    private fun checkCoarseLocationPermission(): Boolean = checkSelfPermission(
        ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
