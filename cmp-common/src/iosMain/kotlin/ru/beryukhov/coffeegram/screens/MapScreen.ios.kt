package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.UIKitInteropInteractionMode
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapPointForCoordinate
import platform.MapKit.MKMapRectMake
import platform.MapKit.MKMapRectUnion
import platform.MapKit.MKMapView
import platform.UIKit.UIEdgeInsetsMake
import ru.beryukhov.coffeegram.components.ExtendedCoffeeShop
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.map.CoffeeShopAnnotation
import ru.beryukhov.coffeegram.map.FitAllMarkersButton
import ru.beryukhov.coffeegram.map.MapDefaults
import ru.beryukhov.coffeegram.map.MapMarker
import ru.beryukhov.coffeegram.map.getZoomLevelOrNull
import ru.beryukhov.coffeegram.map.rememberMapViewDelegate
import ru.beryukhov.coffeegram.map.rememberMkMapView
import ru.beryukhov.coffeegram.map.setCenterAtZoom
import ru.beryukhov.coffeegram.map.toUIImage
import ru.beryukhov.coffeegram.repository.CoffeeShop

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
    showMarkerDescription: Boolean,
) {
    val coffeeShopsState by component.coffeeShops.collectAsState()
    val mkMapView = rememberMkMapView()

    val onZoomChanged by rememberUpdatedState { zoom: Float -> component.onZoomChanged(zoom) }
    val onMarkerClicked by rememberUpdatedState { shop: CoffeeShop -> component.onMarkerClicked(shop) }

    val delegate = rememberMapViewDelegate(
        onVisibleRegionChanged = { mapView ->
            mapView.getZoomLevelOrNull()?.let(onZoomChanged)
        },
        onAnnotationSelected = { annotation ->
            (annotation as? CoffeeShopAnnotation)?.let { onMarkerClicked(it.coffeeShop) }
        }
    )

    LaunchedEffect(mkMapView) {
        mkMapView.setCenterAtZoom(MapDefaults.LATITUDE, MapDefaults.LONGITUDE, MapDefaults.ZOOM)
    }

    val highlightedShop = coffeeShopsState.list.firstOrNull { it.highlighted }?.coffeeShop
    LaunchedEffect(highlightedShop) {
        // Selecting a shop from the list can be off-screen, so pan the map to it. A marker tap
        // already implies the shop is visible, but re-centering on it too is harmless.
        highlightedShop?.let { shop ->
            mkMapView.setCenterCoordinate(
                coordinate = CLLocationCoordinate2DMake(shop.latitude, shop.longitude),
                animated = true,
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            properties = UIKitInteropProperties(
                interactionMode = UIKitInteropInteractionMode.NonCooperative
            ),
            factory = {
                mkMapView.apply {
                    setZoomEnabled(true)
                    setScrollEnabled(true)
                    setRotateEnabled(true)
                    setPitchEnabled(true)
                    setShowsCompass(false)
                    setShowsUserLocation(false)
                }
            },
            update = { mapView ->
                mapView.delegate = delegate
            }
        )

        coffeeShopsState.list.forEach { extended ->
            CoffeeShopMarker(
                mapView = mkMapView,
                extended = extended,
                expanded = coffeeShopsState.expanded,
                showDescription = showMarkerDescription,
            )
        }

        FitAllMarkersButton(
            onClick = {
                panMapToFitAllMarkers(mkMapView, coffeeShopsState.list.map { it.coffeeShop })
            }
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun CoffeeShopMarker(
    mapView: MKMapView,
    extended: ExtendedCoffeeShop,
    expanded: Boolean,
    showDescription: Boolean,
) {
    val density = LocalDensity.current
    val graphicsLayer = rememberGraphicsLayer()
    val coffeeShop = extended.coffeeShop

    val drawModifier = Modifier.drawWithContent {
        graphicsLayer.record { this@drawWithContent.drawContent() }
    }

    Box(modifier = drawModifier) {
        MapMarker(
            name = coffeeShop.name,
            descr = coffeeShop.description,
            highlighted = extended.highlighted,
            expanded = expanded,
            showDescription = showDescription,
        )
    }

    var annotation by remember { mutableStateOf<CoffeeShopAnnotation?>(null) }

    LaunchedEffect(coffeeShop, extended.highlighted, expanded, showDescription) {
        // Wait one frame so Compose's draw phase has populated the graphics layer.
        withFrameNanos { }

        val uiImage = graphicsLayer.toImageBitmap().toUIImage(density.density)

        annotation?.let { mapView.removeAnnotation(it) }

        val reuseIdentifier = "shop-${coffeeShop.latitude}-${coffeeShop.longitude}"
        val coordinate = CLLocationCoordinate2DMake(coffeeShop.latitude, coffeeShop.longitude)

        annotation = CoffeeShopAnnotation(
            coffeeShop = coffeeShop,
            identifier = reuseIdentifier,
            location = coordinate,
            viewCreator = { ann -> MKAnnotationView(annotation = ann, reuseIdentifier = reuseIdentifier) },
            viewBinder = { view ->
                view.image = uiImage
                val size = uiImage.size.useContents { width to height }
                view.centerOffset = CGPointMake(0.0, -size.second / 2.0)
            }
        ).also { mapView.addAnnotation(it) }
    }

    DisposableEffect(coffeeShop) {
        onDispose {
            annotation?.let { mapView.removeAnnotation(it) }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun panMapToFitAllMarkers(mapView: MKMapView, shops: List<CoffeeShop>) {
    when {
        shops.isEmpty() -> {}
        shops.size == 1 -> {
            val only = shops.first()
            mapView.setCenterCoordinate(
                coordinate = CLLocationCoordinate2DMake(only.latitude, only.longitude),
                animated = true,
            )
        }
        else -> {
            val boundingRect = shops
                .map { MKMapPointForCoordinate(CLLocationCoordinate2DMake(it.latitude, it.longitude)) }
                .map { point -> point.useContents { MKMapRectMake(this.x, this.y, 0.0, 0.0) } }
                .reduce { acc, rect -> MKMapRectUnion(acc, rect) }

            val padding = MapDefaults.FIT_PADDING.value.toDouble()
            mapView.setVisibleMapRect(
                boundingRect,
                edgePadding = UIEdgeInsetsMake(padding, padding, padding, padding),
                animated = true,
            )
        }
    }
}
