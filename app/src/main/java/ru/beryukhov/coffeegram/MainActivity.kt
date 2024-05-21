
package ru.beryukhov.coffeegram

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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
import ru.beryukhov.date_time_utils.nowYM
import ru.beryukhov.date_time_utils.toTotalMonths

class MainActivity : ComponentActivity() {

    internal val nodeClient by lazy { Wearable.getNodeClient(this) }
    internal val messageClient by lazy { Wearable.getMessageClient(this) }
    internal val dataClient by lazy { Wearable.getDataClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var doAnimationState by rememberSaveable {
                mutableStateOf(true)
            }
            TransitionSlot(
                doAnimation = doAnimationState,
                StartPage = { modifier -> LandingPage(modifier = modifier) },
                EndPage = { modifier, topPadding ->
                    PagesContent(
                        modifier = modifier,
                        topPadding = topPadding,
                        startWearableActivity = ::startWearableActivity,
                        showMap = checkCoarseLocationPermission()
                    )
                },
            ) {
                doAnimationState = false
            }
        }
    }
}


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

private fun MainActivity.checkCoarseLocationPermission(): Boolean = checkSelfPermission(
    ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

private const val START_ACTIVITY_PATH = "/start-activity"
private const val DAY_COFFEE_PATH = "/coffee"

private const val TAG = "TestWatch_"
