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
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ru.beryukhov.coffeegram.animations.TransitionSlot
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationConstants.NAVIGATION_STATE_KEY
import ru.beryukhov.coffeegram.model.NavigationConstants.TODAYS_COFFEE_LIST
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.screens.RootScreen
import ru.beryukhov.coffeegram.wearable.WearableSyncCoordinator

class MainActivity : ComponentActivity() {

    private val daysCoffeesStore: DaysCoffeesStore by inject()
    private val wearableSyncCoordinator: WearableSyncCoordinator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)

        val themeStore: ThemeStore = get()

        val rootComponent = DefaultRootComponent(
            context = defaultComponentContext(),
            themeStore = themeStore,
            daysCoffeesStore = daysCoffeesStore,
            showMap = true,
            onAndroidStartWearableActivity = wearableSyncCoordinator.wearableActivityStarter,
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
}
