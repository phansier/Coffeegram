@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.get
import ru.beryukhov.coffeegram.animations.TransitionSlot
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.data.toDataMap
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

    internal val nodeClient by lazy { Wearable.getNodeClient(this) }
    internal val messageClient by lazy { Wearable.getMessageClient(this) }
    internal val dataClient by lazy { Wearable.getDataClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TransitionSlot(
                { modifier -> LandingPage(modifier = modifier) },
                { modifier, topPadding ->
                    PagesContent(
                        modifier = modifier,
                        topPadding = topPadding,
                        startWearableActivity = ::startWearableActivity
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent()
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
        themeState = themeState()
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
            Column(modifier = Modifier.padding(it)) {
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
                    is NavigationState.SettingsPage -> SettingsPage(get(), startWearableActivity)
                }
                NavigationBar(modifier = modifier) {
                    NavigationBarItem(selected = currentNavigationState is NavigationState.TablePage, onClick = {
                        navigationStore.newIntent(
                            NavigationIntent.ReturnToTablePage
                        )
                    }, label = { Text(stringResource(id = R.string.calendar)) }, icon = {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "",
                        )
                    })
                    NavigationBarItem(selected = currentNavigationState is NavigationState.SettingsPage, onClick = {
                        navigationStore.newIntent(
                            NavigationIntent.ToSettingsPage
                        )
                    }, label = { Text(stringResource(id = R.string.settings)) }, icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "",
                        )
                    })
                }
            }
        }
    }
}

private const val TAG = "TestWatch_"

private fun MainActivity.startWearableActivity() {
    lifecycleScope.launch {
        try {
            val nodes = nodeClient.connectedNodes.await() // todo depending on nodes count show or hide button

            // Send a message to all nodes in parallel
            nodes.map { node ->
                async {
                    messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf()).await()
                }
            }.awaitAll()

            Log.d(TAG, "Starting activity requests sent successfully")
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Log.d(TAG, "Starting activity failed: $exception")
        }
    }
    sendDayCoffee(
        // todo replace mock
        DayCoffee(
            mapOf(
                CoffeeType.Cappuccino to 1, CoffeeType.Americano to 2
            )
        )
    )
}

private fun MainActivity.sendDayCoffee(dayCoffee: DayCoffee) {
    lifecycleScope.launch {
        try {
            val request = PutDataMapRequest.create(DAY_COFFEE_PATH).apply {
                dayCoffee.toDataMap(dataMap)
            }.asPutDataRequest().setUrgent()

            val result = dataClient.putDataItem(request).await()

            Log.d(TAG, "DataItem saved: $result")
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Log.d(TAG, "Saving DataItem failed: $exception")
        }
    }
}

private const val START_ACTIVITY_PATH = "/start-activity"
private const val DAY_COFFEE_PATH = "/coffee"

@Composable
private fun themeState(): ThemeState {
    val themeState: ThemeState by get<ThemeStore>().state.collectAsState()
    return themeState
}
