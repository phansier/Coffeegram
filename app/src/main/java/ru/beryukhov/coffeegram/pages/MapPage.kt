@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.cameraPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.repository.CoffeeShop
import ru.beryukhov.coffeegram.repository.coffeeShops
import ru.beryukhov.coffeegram.repository.latlng

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

    val viewModel = koinViewModel<MapPageViewModelImpl>()

    if (coarseLocationEnabled) {
        val coffeeShops by viewModel.coffeeShops.collectAsState()

        val cameraPositionState = rememberCameraPositionState {
            position = cameraPosition {
                target(coarseLocation)
                zoom(10f)
            }
        }
        LaunchedEffect(cameraPositionState.position.zoom) {
            viewModel.newZoom(cameraPositionState.position.zoom)
        }
        Box(
            modifier = Modifier.weight(1f),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties().copy(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                cameraPositionState = cameraPositionState
            ) {
                coffeeShops.list.forEach {
                    MarkerComposable(
                        keys = arrayOf(it.highlighted, coffeeShops.expanded),
                        state = MarkerState(it.coffeeShop.latlng()),
                        onClick = { _ ->
                            viewModel.markerClick(it.coffeeShop)
                            true
                        },
                        zIndex = if (it.highlighted) 1f else 0f
                    ) {
                        Marker(
                            name = it.coffeeShop.name,
                            descr = it.coffeeShop.description,
                            highlighted = it.highlighted,
                            expanded = coffeeShops.expanded,
                        )
                    }
                }
            }

            val density = LocalDensity.current.density
            Button(
                onClick = {
                    panMapToFitAllMarkers(coffeeShops.list.map { it.coffeeShop.latlng() }, density)?.let {
                        cameraPositionState.move(
                            it
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Place,
                    contentDescription = ""
                )
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

private fun Dp.toPx(density: Float): Int {
    return (value * density).toInt()
}

fun panMapToFitAllMarkers(locations: List<LatLng>, density: Float): CameraUpdate? =
    when {
        locations.isEmpty() -> {
            null
        }
        locations.size == 1 -> {
            CameraUpdateFactory.newCameraPosition(cameraPosition {
                target(locations.first())
            })
        }
        else -> {
            val latLng = LatLngBounds.builder().apply {
                locations.forEach { include(it) }
            }.build()
            CameraUpdateFactory.newLatLngBounds(
                /* bounds = */ latLng,
                /* padding = */ 72.dp.toPx(density)
            )
        }
    }

@Composable
@Preview
private fun SmallMarker() = Marker(expanded = false)

@Composable
@Preview
private fun ExpandedMarker() = Marker(expanded = true)

@Composable
fun Marker(
    modifier: Modifier = Modifier,
    name: String = "Title",
    descr: String = "Subtitle",
    highlighted: Boolean = false,
    expanded: Boolean = false,
) {
    val borderRadius = if (expanded) 12.dp else 6.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .boxShadow(
                blurRadius = 3.dp,
                offset = DpOffset(x = 0.dp, y = 2.dp),
                shape = RoundedCornerShape(borderRadius),
                color = Color(0f, 0f, 0f, 0.15f)
            )
            .boxShadow(
                blurRadius = 9.dp,
                offset = DpOffset(x = 0.dp, y = 6.dp),
                shape = RoundedCornerShape(borderRadius),
                color = Color(0f, 0f, 0f, 0.04f)
            )
//            .height(if (expanded) 44.dp else 30.dp)
            .background(
                color = if (highlighted) Color(0xFFE8E5E3) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 6.dp)
            )
            .padding(start = 4.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
            .widthIn(min = 0.dp, max = (LocalConfiguration.current.screenWidthDp / if (highlighted) 1 else 2).dp)

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
            verticalArrangement = Arrangement.spacedBy(if (highlighted) 4.dp else -4.dp, Alignment.CenterVertically),
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
                ),
                maxLines = if (highlighted) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
            if (expanded) {
                Text(
                    text = descr,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(350),
                        color = textColor,
                    ),
                    maxLines = if (highlighted) 3 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
    val coffeeShops: StateFlow<CoffeeShopsState>

    fun newZoom(zoom: Float)
    fun markerClick(coffeeShop: CoffeeShop)
}

class MapPageViewModelImpl : ViewModel(), MapPageViewModel {
    override val coffeeShops: MutableStateFlow<CoffeeShopsState> by lazy {
        val m = MutableStateFlow(CoffeeShopsState(emptyList(), false))
        viewModelScope.launch {
            m.tryEmit(CoffeeShopsState(coffeeShops().map { ExtendedCoffeeShop(it, false) }, false))
        }
        m
    }

    override fun newZoom(zoom: Float) {
        Log.d("MapPageViewModelImpl", "newZoom: $zoom")
        val newExpanded = zoom >= 11
        if (coffeeShops.value.expanded != newExpanded) {
            coffeeShops.tryEmit(coffeeShops.value.copy(expanded = newExpanded))
        }
    }

    override fun markerClick(coffeeShop: CoffeeShop) {
        Log.d("MapPageViewModelImpl", "markerClick: $coffeeShop")
        coffeeShops.tryEmit(coffeeShops.value.copy(list = coffeeShops.value.list.map {
            if (it.coffeeShop == coffeeShop) {
                it.copy(highlighted = true)
            } else {
                it.copy(highlighted = false)
            }
        }))
    }
}

data class CoffeeShopsState(
    val list: List<ExtendedCoffeeShop>,
    val expanded: Boolean
)

data class ExtendedCoffeeShop(
    val coffeeShop: CoffeeShop,
    val highlighted: Boolean,
)
