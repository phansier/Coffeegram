package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.arkivanov.decompose.DefaultComponentContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.newapp.DefaultRootComponent
import ru.beryukhov.coffeegram.newapp.NewApp
import ru.beryukhov.coffeegram.pages.LandingPage

class MainActivity : ComponentActivity() {

    private val navigationStore: NavigationStore by inject()
    private val daysCoffeesStore: DaysCoffeesStore by inject()
    private val themeStore: ThemeStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val transition = newSplashTransition()
            Box {
                LandingPage(
                    modifier = Modifier.alpha(transition.splashAlpha),
                )
                NewApp(
                    rootComponent = DefaultRootComponent(
                        DefaultComponentContext(lifecycle = lifecycle),
                        themeStore = get()
                    ),
                    modifier = Modifier.alpha(transition.contentAlpha),
                )
//                PagesContent(
//                    modifier = Modifier.alpha(transition.contentAlpha),
//                    topPadding = transition.contentTopPadding,
//                    navigationStore = navigationStore,
//                    daysCoffeesStore = daysCoffeesStore,
//                    themeStore = themeStore,
//                )
            }
        }
    }
}
