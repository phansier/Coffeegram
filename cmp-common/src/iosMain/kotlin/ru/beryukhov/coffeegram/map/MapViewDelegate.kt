package ru.beryukhov.coffeegram.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ObjCSignatureOverride
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.darwin.NSObject

@Composable
internal fun rememberMapViewDelegate(
    onVisibleRegionChanged: (MKMapView) -> Unit,
    onAnnotationSelected: (MKAnnotationProtocol) -> Unit,
): MKMapViewDelegateProtocol {
    val currentOnVisibleRegionChanged by rememberUpdatedState(onVisibleRegionChanged)
    val currentOnAnnotationSelected by rememberUpdatedState(onAnnotationSelected)

    return remember {
        object : NSObject(), MKMapViewDelegateProtocol {

            override fun mapViewDidChangeVisibleRegion(mapView: MKMapView) =
                currentOnVisibleRegionChanged(mapView)

            @Suppress("CONFLICTING_OVERLOADS", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            @ObjCSignatureOverride
            override fun mapView(
                mapView: MKMapView,
                viewForAnnotation: MKAnnotationProtocol,
            ): MKAnnotationView? {
                val annotation = viewForAnnotation as? CoffeeShopAnnotation ?: return null

                var annotationView = mapView.dequeueReusableAnnotationViewWithIdentifier(annotation.identifier)
                if (annotationView == null) {
                    annotationView = annotation.createView()
                } else {
                    annotationView.annotation = viewForAnnotation
                }
                annotation.bindView(annotationView)
                return annotationView
            }

            @Suppress("CONFLICTING_OVERLOADS", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            @ObjCSignatureOverride
            override fun mapView(mapView: MKMapView, didSelectAnnotation: MKAnnotationProtocol) {
                currentOnAnnotationSelected(didSelectAnnotation)
            }
        }
    }
}
