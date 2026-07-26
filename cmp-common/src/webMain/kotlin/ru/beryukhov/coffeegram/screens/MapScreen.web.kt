package ru.beryukhov.coffeegram.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement
import ru.beryukhov.coffeegram.app_ui.LocalDarkTheme
import ru.beryukhov.coffeegram.components.ExtendedCoffeeShop
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.map.MapDefaults
import ru.beryukhov.coffeegram.repository.CoffeeShop

// Adapted from https://github.com/Beriukhov/h3-kmp commonSample/H3MapView.wasmJs.kt.
// Overlays a fixed-position MapLibre <div> over the Compose Box's layout slot. All JS interop
// (creating the map, adding markers, etc.) is hidden behind `expect`/`actual` so both the
// wasmJs and legacy JS targets share this file.

private const val MAPLIBRE_VERSION = "4.7.1"
private const val LIGHT_STYLE_URL = "https://tiles.openfreemap.org/styles/positron"
private const val DARK_STYLE_URL = "https://tiles.openfreemap.org/styles/fiord"

private var nextMapId: Int = 0
private var maplibreLoadStarted: Boolean = false
private val maplibreLoadCallbacks: MutableList<() -> Unit> = mutableListOf()

@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
    showMarkerDescription: Boolean,
) {
    val coffeeShopsState by component.coffeeShops.collectAsState()
    val containerId = remember { "coffee-map-${nextMapId++}" }
    val darkTheme = LocalDarkTheme.current

    val onMarkerClicked by rememberUpdatedState { shop: CoffeeShop -> component.onMarkerClicked(shop) }
    val onZoomChanged by rememberUpdatedState { zoom: Float -> component.onZoomChanged(zoom) }
    val onUserLocationObtained by rememberUpdatedState { component.onUserLocationObtained() }

    val state = remember {
        WebMapState(
            containerId = containerId,
            initialDarkTheme = darkTheme,
            onMarkerClicked = { shop -> onMarkerClicked(shop) },
            onZoomChanged = { zoom -> onZoomChanged(zoom) },
            onUserLocationObtained = { onUserLocationObtained() },
        )
    }

    DisposableEffect(state) {
        state.attach()
        onDispose { state.detach() }
    }

    LaunchedEffect(coffeeShopsState.list, coffeeShopsState.expanded, showMarkerDescription) {
        state.updateMarkers(coffeeShopsState.list, coffeeShopsState.expanded, showMarkerDescription)
    }

    LaunchedEffect(darkTheme) {
        state.updateDarkTheme(darkTheme)
    }

    val highlightedShop = coffeeShopsState.list.firstOrNull { it.highlighted }?.coffeeShop
    LaunchedEffect(highlightedShop) {
        // Selecting a shop from the list can be off-screen, so pan the map to it. A marker click
        // already implies the shop is visible, but re-centering on it too is harmless.
        highlightedShop?.let { shop -> state.focusOnShop(shop.longitude, shop.latitude) }
    }

    Box(
        modifier = modifier
            .background(Color(0xFFE6E6E6))
            .onGloballyPositioned { coords ->
                val p = coords.positionInWindow()
                val dpr = window.devicePixelRatio.takeIf { it > 0.0 } ?: 1.0
                state.updateGeometry(
                    x = p.x.toDouble() / dpr,
                    y = p.y.toDouble() / dpr,
                    w = coords.size.width.toDouble() / dpr,
                    h = coords.size.height.toDouble() / dpr,
                )
            }
            .fillMaxSize()
    )
}

private class WebMapState(
    private val containerId: String,
    initialDarkTheme: Boolean,
    private val onMarkerClicked: (CoffeeShop) -> Unit,
    private val onZoomChanged: (Float) -> Unit,
    private val onUserLocationObtained: () -> Unit,
) {
    private var div: HTMLDivElement? = null
    private var map: MapHandle? = null
    private var ready: Boolean = false
    private var pendingShops: List<ExtendedCoffeeShop> = emptyList()
    private var pendingExpanded: Boolean = false
    private var pendingShowDescription: Boolean = true
    private var didInitialFit: Boolean = false
    private var darkTheme: Boolean = initialDarkTheme
    private var lastX = Double.NaN
    private var lastY = Double.NaN
    private var lastW = Double.NaN
    private var lastH = Double.NaN
    private var userLocation: Pair<Double, Double>? = null
    private var didFocusUserLocation: Boolean = false
    private var currentZoom: Double = MapDefaults.ZOOM.toDouble()

    fun attach() {
        val d = (document.createElement("div") as HTMLDivElement).apply {
            id = containerId
            style.position = "fixed"
            style.left = "0px"
            style.top = "0px"
            style.width = "0px"
            style.height = "0px"
            style.zIndex = "2147483647"
            style.background = "#e6e6e6"
        }
        document.documentElement?.appendChild(d)
        div = d
        jsGetCurrentPosition(
            onSuccess = { lng, lat -> onUserLocationResolved(lng, lat) },
            onError = {},
        )
    }

    private fun onUserLocationResolved(lng: Double, lat: Double) {
        userLocation = lng to lat
        onUserLocationObtained()
        if (didFocusUserLocation) return
        val m = map ?: return
        jsSetCenter(m, lng, lat, MapDefaults.ZOOM_WITH_LOCATION.toDouble())
        didFocusUserLocation = true
    }

    fun focusOnShop(lng: Double, lat: Double) {
        val m = map ?: return
        jsSetCenter(m, lng, lat, currentZoom)
    }

    fun detach() {
        map?.let { jsRemoveMap(it) }
        map = null
        div?.remove()
        div = null
        ready = false
    }

    fun updateMarkers(shops: List<ExtendedCoffeeShop>, expanded: Boolean, showDescription: Boolean) {
        pendingShops = shops
        pendingExpanded = expanded
        pendingShowDescription = showDescription
        if (ready) applyPending()
    }

    fun updateDarkTheme(dark: Boolean) {
        if (dark == darkTheme) return
        darkTheme = dark
        map?.let { jsSetStyle(it, styleUrl()) }
    }

    private fun styleUrl(): String = if (darkTheme) DARK_STYLE_URL else LIGHT_STYLE_URL

    fun updateGeometry(x: Double, y: Double, w: Double, h: Double) {
        if (x == lastX && y == lastY && w == lastW && h == lastH) return
        lastX = x; lastY = y; lastW = w; lastH = h
        div?.let {
            it.style.left = "${x}px"
            it.style.top = "${y}px"
            it.style.width = "${w}px"
            it.style.height = "${h}px"
        }
        if (w <= 0.0 || h <= 0.0) return
        if (map == null) {
            ensureMaplibreLoaded {
                val d = div ?: return@ensureMaplibreLoaded
                if (map != null) return@ensureMaplibreLoaded
                val location = userLocation
                currentZoom = (location?.let { MapDefaults.ZOOM_WITH_LOCATION } ?: MapDefaults.ZOOM).toDouble()
                val m = jsCreateMap(
                    containerId = containerId,
                    styleUrl = styleUrl(),
                    lng = location?.first ?: MapDefaults.LONGITUDE,
                    lat = location?.second ?: MapDefaults.LATITUDE,
                    zoom = currentZoom,
                )
                map = m
                if (location != null) didFocusUserLocation = true
                jsObserveResize(d, m)
                jsAttachZoomHandler(m) { zoom ->
                    currentZoom = zoom
                    onZoomChanged(zoom.toFloat())
                }
                jsAttachLoadHandler(m) {
                    ready = true
                    jsResizeMap(m)
                    applyPending()
                }
            }
        } else {
            jsResizeMap(map!!)
        }
    }

    private fun applyPending() {
        val m = map ?: return
        jsClearMarkers(m)
        for (extended in pendingShops) {
            val shop = extended.coffeeShop
            jsAddMarker(
                map = m,
                lng = shop.longitude,
                lat = shop.latitude,
                title = shop.name,
                description = if (pendingExpanded && pendingShowDescription) shop.description else "",
                highlighted = extended.highlighted,
                onClick = { onMarkerClicked(shop) },
            )
        }
        // Only fit-to-bounds once, on the first non-empty load. Subsequent marker rebuilds
        // (from expand/highlight state changes) must not stomp on the user's pan/zoom, and a
        // resolved user location takes priority over fitting to all markers.
        if (!didInitialFit && !didFocusUserLocation && pendingShops.isNotEmpty()) {
            val b = paddedBounds(pendingShops.map { it.coffeeShop })
            jsFitBounds(m, b.west, b.south, b.east, b.north)
            didInitialFit = true
        }
    }
}

private data class Bounds(val west: Double, val south: Double, val east: Double, val north: Double)

private fun paddedBounds(shops: List<CoffeeShop>): Bounds {
    var minLat = shops[0].latitude; var maxLat = shops[0].latitude
    var minLng = shops[0].longitude; var maxLng = shops[0].longitude
    for (s in shops) {
        if (s.latitude < minLat) minLat = s.latitude
        if (s.latitude > maxLat) maxLat = s.latitude
        if (s.longitude < minLng) minLng = s.longitude
        if (s.longitude > maxLng) maxLng = s.longitude
    }
    val padLat = ((maxLat - minLat) * 0.1).coerceAtLeast(0.001)
    val padLng = ((maxLng - minLng) * 0.1).coerceAtLeast(0.001)
    return Bounds(minLng - padLng, minLat - padLat, maxLng + padLng, maxLat + padLat)
}

private fun ensureMaplibreLoaded(callback: () -> Unit) {
    if (jsMaplibreReady()) {
        callback()
        return
    }
    maplibreLoadCallbacks.add(callback)
    if (maplibreLoadStarted) return
    maplibreLoadStarted = true

    jsInjectCriticalCss()

    val link = (document.createElement("link") as HTMLLinkElement).apply {
        rel = "stylesheet"
        href = "https://unpkg.com/maplibre-gl@$MAPLIBRE_VERSION/dist/maplibre-gl.css"
    }
    document.head?.appendChild(link)

    val script = (document.createElement("script") as HTMLScriptElement).apply {
        src = "https://unpkg.com/maplibre-gl@$MAPLIBRE_VERSION/dist/maplibre-gl.js"
    }
    jsSetOnLoad(script) {
        val cbs = maplibreLoadCallbacks.toList()
        maplibreLoadCallbacks.clear()
        cbs.forEach { it() }
    }
    document.head?.appendChild(script)
}
