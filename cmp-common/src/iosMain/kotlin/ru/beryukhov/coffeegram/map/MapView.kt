package ru.beryukhov.coffeegram.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round

private const val FULL_ROTATION_DEGREES = 360.0
private const val TILE_SIZE = 256.0

@Composable
internal fun rememberMkMapView(): MKMapView {
    val mapView = remember { MKMapView() }

    DisposableEffect(mapView) {
        onDispose {
            mapView.setDelegate(null)
            mapView.removeAnnotations(mapView.annotations)
        }
    }

    return mapView
}

/**
 * Approximates Google Maps zoom level from the current visible region. Returns null if the
 * map view hasn't been laid out yet (zero frame or longitude delta), so callers can ignore
 * spurious values during the initial layout pass.
 */
@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.getZoomLevelOrNull(): Float? {
    val width = frame.useContents { size.width }
    val longitudeDelta = region.useContents { span.longitudeDelta }
    if (longitudeDelta <= 0.0 || width <= 0.0) return null
    return round(log2(FULL_ROTATION_DEGREES * (width / TILE_SIZE) / longitudeDelta)).toFloat()
}

@OptIn(ExperimentalForeignApi::class)
internal fun MKMapView.setCenterAtZoom(latitude: Double, longitude: Double, zoomLevel: Float) {
    val width = frame.useContents { size.width }.coerceAtLeast(TILE_SIZE)
    val span = MKCoordinateSpanMake(
        latitudeDelta = 0.0,
        longitudeDelta = FULL_ROTATION_DEGREES / 2.0.pow(zoomLevel.toDouble()) * (width / TILE_SIZE)
    )
    val center = platform.CoreLocation.CLLocationCoordinate2DMake(latitude, longitude)
    setRegion(MKCoordinateRegionMake(center, span), animated = false)
}
