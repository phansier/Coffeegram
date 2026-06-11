package ru.beryukhov.coffeegram.screens

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.map.FitAllMarkersButton
import ru.beryukhov.coffeegram.map.MapDefaults
import ru.beryukhov.coffeegram.map.MapMarker
import ru.beryukhov.coffeegram.repository.CoffeeShop

@SuppressLint("MissingPermission")
@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
    showMarkerDescription: Boolean,
) {
    val context = LocalContext.current
    val coarseLocationEnabled = remember {
        context.checkSelfPermission(
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    val coarseLocation = remember {
        val locationDefault = LatLng(MapDefaults.LATITUDE, MapDefaults.LONGITUDE)
        val coarseLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            coarseLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?.let { LatLng(it.latitude, it.longitude) } ?: locationDefault
        } catch (_: SecurityException) {
            locationDefault
        }
    }

    val coffeeShopsState by component.coffeeShops.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = cameraPosition {
            // Center on the user's coarse location when granted, otherwise the default location.
            target(if (coarseLocationEnabled) coarseLocation else LatLng(MapDefaults.LATITUDE, MapDefaults.LONGITUDE))
            zoom(MapDefaults.ZOOM)
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
                isMyLocationEnabled = coarseLocationEnabled
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
                    keys = arrayOf(it.highlighted, coffeeShopsState.expanded, showMarkerDescription),
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
                        showDescription = showMarkerDescription,
                    )
                }
            }
        }

        val density = LocalDensity.current.density
        FitAllMarkersButton(
            onClick = {
                panMapToFitAllMarkers(coffeeShopsState.list.map { it.coffeeShop.latlng() }, density)?.let {
                    cameraPositionState.move(it)
                }
            }
        )
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
                (MapDefaults.FIT_PADDING.value * density).toInt()
            )
        }
    }

fun CoffeeShop.latlng() = LatLng(latitude, longitude)
