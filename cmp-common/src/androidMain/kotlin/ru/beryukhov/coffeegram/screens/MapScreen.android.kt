@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coffeegram.cmp_common.generated.resources.Res
import coffeegram.cmp_common.generated.resources.location_permission_required
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
import org.jetbrains.compose.resources.stringResource
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.map.MapMarker
import ru.beryukhov.coffeegram.repository.CoffeeShop

@SuppressLint("MissingPermission")
@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
) {
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

    if (coarseLocationEnabled) {
        val coffeeShopsState by component.coffeeShops.collectAsState()

        val cameraPositionState = rememberCameraPositionState {
            position = cameraPosition {
                target(coarseLocation)
                zoom(10f)
            }
        }
        LaunchedEffect(cameraPositionState.position.zoom) {
            component.onZoomChanged(cameraPositionState.position.zoom)
        }
        Box(
            modifier = modifier.fillMaxSize(),
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
                coffeeShopsState.list.forEach {
                    MarkerComposable(
                        keys = arrayOf(it.highlighted, coffeeShopsState.expanded),
                        state = MarkerState(it.coffeeShop.latlng()),
                        onClick = { _ ->
                            component.onMarkerClicked(it.coffeeShop)
                            true
                        },
                        zIndex = if (it.highlighted) 1f else 0f
                    ) {
                        MapMarker(
                            name = it.coffeeShop.name,
                            descr = it.coffeeShop.description,
                            highlighted = it.highlighted,
                            expanded = coffeeShopsState.expanded,
                        )
                    }
                }
            }

            val density = LocalDensity.current.density
            Button(
                onClick = {
                    panMapToFitAllMarkers(coffeeShopsState.list.map { it.coffeeShop.latlng() }, density)?.let {
                        cameraPositionState.move(it)
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
            Text(
                text = stringResource(Res.string.location_permission_required),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun panMapToFitAllMarkers(locations: List<LatLng>, density: Float): CameraUpdate? =
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
                latLng,
                (72 * density).toInt()
            )
        }
    }

fun CoffeeShop.latlng() = LatLng(latitude, longitude)
