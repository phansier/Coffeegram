package ru.beryukhov.coffeegram.map

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Constants shared between the Android (Google Maps) and iOS (MapKit) implementations of
 * `MapScreen` so both platforms start at the same camera and behave the same way when the
 * "fit all markers" action is used.
 */
object MapDefaults {
    /** Initial map camera latitude (Cyprus). */
    const val LATITUDE: Double = 35.1272

    /** Initial map camera longitude (Cyprus). */
    const val LONGITUDE: Double = 33.3371

    /** Initial Google-Maps-style zoom level used for the very first camera position. */
    const val ZOOM: Float = 10f
    const val ZOOM_WITH_LOCATION: Float = 13f

    /** Screen-space padding added around the bounding box when fitting all markers. */
    val FIT_PADDING: Dp = 72.dp
}
