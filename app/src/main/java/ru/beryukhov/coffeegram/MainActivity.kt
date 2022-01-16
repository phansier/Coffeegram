package ru.beryukhov.coffeegram

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.get
import ru.beryukhov.coffeegram.animations.newSplashTransition
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationState
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.model.ThemeState
import ru.beryukhov.coffeegram.model.ThemeStore
import ru.beryukhov.coffeegram.pages.CoffeeListAppBar
import ru.beryukhov.coffeegram.pages.CoffeeListPage
import ru.beryukhov.coffeegram.pages.LandingPage
import ru.beryukhov.coffeegram.pages.SettingsAppBar
import ru.beryukhov.coffeegram.pages.SettingsPage
import ru.beryukhov.coffeegram.pages.TableAppBar
import ru.beryukhov.coffeegram.pages.TablePage

class MainActivity : AppCompatActivity() {

    val nodeClient by lazy { Wearable.getNodeClient(this) }
    val messageClient by lazy { Wearable.getMessageClient(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.support_simple_spinner_dropdown_item)
        setContent {
            val transition = newSplashTransition()
            Box {
                LandingPage(
                    modifier = Modifier.alpha(transition.splashAlpha),
                )
                PagesContent(
                    modifier = Modifier.alpha(transition.contentAlpha),
                    topPadding = transition.contentTopPadding,
                    startWearableActivity = ::startWearableActivity
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(
    )
}

@Composable
fun PagesContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    navigationStore: NavigationStore = get(),
    startWearableActivity: () -> Unit = {}
) {
    val navigationState: NavigationState by navigationStore.state.collectAsState()
    val currentNavigationState = navigationState
    CoffeegramTheme(
        darkTheme = isDarkTheme()
    ) {
        Scaffold(
            modifier,
            topBar = {
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TableAppBar(
                        yearMonth = currentNavigationState.yearMonth,
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListAppBar(
                        localDate = currentNavigationState.date
                    )
                    is NavigationState.SettingsPage -> SettingsAppBar()
                }
            },
        ) {
            Column {
                Spacer(
                    Modifier
                        .padding(top = topPadding)
                        .align(Alignment.CenterHorizontally)
                )
                when (currentNavigationState) {
                    is NavigationState.TablePage -> TablePage(
                        yearMonth = currentNavigationState.yearMonth
                    )
                    is NavigationState.CoffeeListPage -> CoffeeListPage(
                        localDate = currentNavigationState.date
                    )
                    is NavigationState.SettingsPage -> {
                        SettingsPage(get(), startWearableActivity)
                    }
                }
                BottomNavigation(modifier = modifier) {
                    BottomNavigationItem(selected = currentNavigationState is NavigationState.TablePage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ReturnToTablePage
                            )
                        },
                        label = { Text(stringResource(id = R.string.calendar)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "",
                            )
                        }
                    )
                    BottomNavigationItem(selected = currentNavigationState is NavigationState.SettingsPage,
                        onClick = {
                            navigationStore.newIntent(
                                NavigationIntent.ToSettingsPage
                            )
                        },
                        label = { Text(stringResource(id = R.string.settings)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "",
                            )
                        }
                    )
                }
            }
        }
    }
}

private val TAG = "TestWatch_"

private fun MainActivity.startWearableActivity() {
    lifecycleScope.launch {
        try {
            val nodes = nodeClient.connectedNodes.await()

            // Send a message to all nodes in parallel
            nodes.map { node ->
                async {
                    messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf())
                        .await()
                }
            }.awaitAll()

            Log.d(TAG, "Starting activity requests sent successfully")
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Log.d(TAG, "Starting activity failed: $exception")
        }
    }
}

private const val START_ACTIVITY_PATH = "/start-activity"

@Composable
private fun isDarkTheme(): Boolean {
    val themeState: ThemeState by get<ThemeStore>().state.collectAsState()
    return when (themeState) {
        ThemeState.DARK -> true
        ThemeState.LIGHT -> false
        ThemeState.SYSTEM -> isSystemInDarkTheme()
    }
}
