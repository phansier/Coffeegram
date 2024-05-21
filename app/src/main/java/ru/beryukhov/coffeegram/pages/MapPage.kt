@file:OptIn(ExperimentalMaterial3Api::class)

package ru.beryukhov.coffeegram.pages


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import ru.beryukhov.coffeegram.R

@Preview
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(/* target = */ coarseLocation, /* zoom = */ 10f)
    }
    if (coarseLocationEnabled) {
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            properties = MapProperties().copy(isMyLocationEnabled = true),
            cameraPositionState = cameraPositionState
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            Text(text = "Your should give location permission", modifier = Modifier.align(Alignment.Center))
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
