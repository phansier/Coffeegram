@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.repository.CoffeeShop
import ru.beryukhov.coffeegram.repository.coffeeShops

@Composable
internal fun MapPagePreview() {
    Column {
        MapPage()
    }
}

@Composable
fun ColumnScope.MapPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coarseLocationEnabled = remember {
        context.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    val coarseLocation = remember {
        val locationDefault = LatLng(35.1272, 33.3371)
        val coarseLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?.let { LatLng(it.latitude, it.longitude) } ?: locationDefault
        } catch (_: SecurityException) {
            locationDefault
        }
    }


    val coffeeShops by koinViewModel<MapPageViewModelImpl>().coffeeShops.collectAsState()


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(/* target = */ coarseLocation, /* zoom = */ 10f)
    }
    if (coarseLocationEnabled) {
        Box(
            modifier = modifier.weight(1f),
        ) {
            GoogleMap(
                modifier = modifier.fillMaxSize(),
                properties = MapProperties().copy(isMyLocationEnabled = true),
                cameraPositionState = cameraPositionState
            ) {
                val zoom by remember { mutableFloatStateOf(cameraPositionState.position.zoom) }
                coffeeShops.forEach {
                    var state by mutableStateOf(ExtendedMarkerState(
                        position = LatLng(it.latitude, it.longitude),
                        highlighted = false,
                        expanded = zoom >= 11f
                    ))
                    MarkerComposable(
                        state = state.markerState,
                        ) {
                        Marker(
                            name = it.name,
                            highlighted = state.highlighted,
                            expanded = cameraPositionState.position.zoom >= 11f,
                            onClick = { state = state.copy(highlighted = !state.highlighted) })

                    }
                }

            }
        }

    } else {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            Text(text = "Your should give location permission", modifier = Modifier.align(Alignment.Center))
        }

    }
}

data class ExtendedMarkerState(
    val position: LatLng,
    val highlighted: Boolean,
    val expanded: Boolean
) {
    val markerState: MarkerState get() = MarkerState(position)
}


@Composable
@Preview
fun Marker(name: String = "Title", descr: String = "Subtitle", highlighted: Boolean = false, expanded: Boolean = true, onClick: () -> Unit = { }) =
    Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .padding(16.dp)
        .shadow(elevation = 9.dp, shape = RoundedCornerShape(size = 6.dp))
        .height(if (expanded) 44.dp else 30.dp)
        .background(
            color = if (highlighted) Color(0xFFE8E5E3) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(size = 6.dp)
        )
        .padding(start = 4.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
        .clickable { onClick() }
) {
    Image(
        painter = painterResource(id = R.drawable.logo_splash),
        contentDescription = "image description",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .padding(start = 0.dp, top = 1.dp, end = 2.dp, bottom = 1.dp)
            .width(18.dp)
            .height(18.dp)
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(-4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(start = 4.dp)
    ) {
        val textColor = /*Color(0xFF003DD9)*/ MaterialTheme.colorScheme.onPrimaryContainer
        Text(
            text = name,
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(350),
                color = textColor,
            )
        )
        if (expanded) {
            Text(
                text = descr,
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight(350),
                    color = textColor,
                )
            )
        }
    }

}

@Composable
fun MapAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.map_long)) },
        modifier = modifier
    )
}

interface MapPageViewModel {
    val coffeeShops: StateFlow<List<CoffeeShop>>
}

class MapPageViewModelImpl : ViewModel(), MapPageViewModel {
    override val coffeeShops: StateFlow<List<CoffeeShop>> by lazy {
        val m = MutableStateFlow<List<CoffeeShop>>(emptyList())
        viewModelScope.launch {
            m.tryEmit(coffeeShops())
        }
        m
    }
}
