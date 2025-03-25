package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.ext.android.get
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.components.DefaultRootComponent
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.screens.RootScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootComponent = DefaultRootComponent(
            defaultComponentContext(),
            themeStore = get(),
            daysCoffeesStore = get(),
        )
        setContent {
            val transition = newSplashTransition()
            Box {
                LandingPage(
                    modifier = Modifier.alpha(transition.splashAlpha),
                )
                RootScreen(
                    rootComponent,
                    modifier = Modifier.alpha(transition.contentAlpha),
                )
            }
        }
    }
}
