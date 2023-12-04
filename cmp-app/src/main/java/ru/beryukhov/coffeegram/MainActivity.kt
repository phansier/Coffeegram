package ru.beryukhov.coffeegram

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import org.koin.android.ext.android.inject
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.LandingPage

class MainActivity : AppCompatActivity() {

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
                PagesContent(
                    modifier = Modifier.alpha(transition.contentAlpha),
                    topPadding = transition.contentTopPadding,
                    navigationStore = navigationStore,
                    daysCoffeesStore = daysCoffeesStore,
                    themeStore = themeStore,
                )
            }
        }
    }
}
