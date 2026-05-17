@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

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
import ru.beryukhov.coffeegram.components.ExtendedCoffeeShop
import ru.beryukhov.coffeegram.components.MapComponent
import ru.beryukhov.coffeegram.map.MapDefaults
import ru.beryukhov.coffeegram.repository.CoffeeShop

// Adapted from https://github.com/Beriukhov/h3-kmp commonSample/H3MapView.wasmJs.kt.
// Overlays a fixed-position MapLibre <div> at the location of the Compose Box and re-syncs
// its geometry whenever the layout changes.

private const val MAPLIBRE_VERSION = "4.7.1"
private const val DEFAULT_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

private var nextMapId: Int = 0
private var maplibreLoadStarted: Boolean = false
private val maplibreLoadCallbacks: MutableList<() -> Unit> = mutableListOf()

@Composable
actual fun MapScreen(
    component: MapComponent,
    modifier: Modifier,
) {
    val coffeeShopsState by component.coffeeShops.collectAsState()
    val containerId = remember { "coffee-map-${nextMapId++}" }

    val onMarkerClicked by rememberUpdatedState { shop: CoffeeShop -> component.onMarkerClicked(shop) }
    val onZoomChanged by rememberUpdatedState { zoom: Float -> component.onZoomChanged(zoom) }

    val state = remember {
        WasmMapState(
            containerId = containerId,
            onMarkerClicked = { shop -> onMarkerClicked(shop) },
            onZoomChanged = { zoom -> onZoomChanged(zoom) },
        )
    }

    DisposableEffect(state) {
        state.attach()
        onDispose { state.detach() }
    }

    LaunchedEffect(coffeeShopsState.list, coffeeShopsState.expanded) {
        state.updateMarkers(coffeeShopsState.list, coffeeShopsState.expanded)
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

private class WasmMapState(
    private val containerId: String,
    private val onMarkerClicked: (CoffeeShop) -> Unit,
    private val onZoomChanged: (Float) -> Unit,
) {
    private var div: HTMLDivElement? = null
    private var map: JsAny? = null
    private var ready: Boolean = false
    private var pendingShops: List<ExtendedCoffeeShop> = emptyList()
    private var pendingExpanded: Boolean = false
    private var lastX = Double.NaN
    private var lastY = Double.NaN
    private var lastW = Double.NaN
    private var lastH = Double.NaN

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
    }

    fun detach() {
        map?.let { jsRemoveMap(it) }
        map = null
        div?.remove()
        div = null
        ready = false
    }

    fun updateMarkers(shops: List<ExtendedCoffeeShop>, expanded: Boolean) {
        pendingShops = shops
        pendingExpanded = expanded
        if (ready) applyPending()
    }

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
                val m = jsCreateMap(
                    containerId,
                    DEFAULT_STYLE_URL,
                    MapDefaults.LONGITUDE,
                    MapDefaults.LATITUDE,
                    MapDefaults.ZOOM.toDouble(),
                )
                map = m
                jsObserveResize(d, m)
                jsAttachZoomHandler(m) { zoom -> onZoomChanged(zoom.toFloat()) }
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
                description = if (pendingExpanded) shop.description else "",
                highlighted = extended.highlighted,
                onClick = { onMarkerClicked(shop) },
            )
        }
        if (pendingShops.isNotEmpty()) {
            val b = paddedBounds(pendingShops.map { it.coffeeShop })
            jsFitBounds(m, b.west, b.south, b.east, b.north)
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

private fun jsInjectCriticalCss() {
    js(
        "(function(){" +
            "var s = document.createElement('style');" +
            "s.textContent = " +
                "'.maplibregl-map{position:relative;overflow:hidden;}' +" +
                "'.maplibregl-canvas-container{position:absolute;left:0;top:0;width:100%;height:100%;}' +" +
                "'.maplibregl-canvas-container canvas{position:absolute;left:0;top:0;}' +" +
                "'.maplibregl-canvas{position:absolute;left:0;top:0;width:100%;height:100%;}';" +
            "document.head.appendChild(s);" +
        "})()"
    )
}

private fun jsMaplibreReady(): Boolean =
    js("(typeof maplibregl !== 'undefined')")

private fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit) {
    js("(el.onload = function() { cb(); })")
}

private fun jsCreateMap(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): JsAny =
    js(
        "(new maplibregl.Map({" +
            "container: containerId," +
            "style: styleUrl," +
            "center: [lng, lat]," +
            "zoom: zoom" +
        "}))"
    )

private fun jsAttachLoadHandler(map: JsAny, cb: () -> Unit) {
    js("(function(){ if (map.loaded()) { cb(); } else { map.on('load', function(){ cb(); }); } })()")
}

private fun jsAttachZoomHandler(map: JsAny, cb: (Double) -> Unit) {
    js("map.on('zoom', function(){ cb(map.getZoom()); })")
}

private fun jsClearMarkers(map: JsAny) {
    js("(function(){ if (map.__cgMarkers) { map.__cgMarkers.forEach(function(m){ m.remove(); }); } map.__cgMarkers = []; })()")
}

private fun jsAddMarker(
    map: JsAny,
    lng: Double,
    lat: Double,
    title: String,
    description: String,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    js(
        "(function(){" +
            "var el = document.createElement('div');" +
            "el.style.cssText = 'display:flex;flex-direction:column;align-items:flex-start;background:' + (highlighted ? '#E8E5E3' : '#FFFFFF') + ';border-radius:6px;padding:3px 8px;box-shadow:0 2px 3px rgba(0,0,0,0.15),0 6px 9px rgba(0,0,0,0.04);font-family:sans-serif;cursor:pointer;max-width:240px;';" +
            "var name = document.createElement('div');" +
            "name.textContent = title;" +
            "name.style.cssText = 'font-size:14px;font-weight:500;color:#1F1B16;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:220px;';" +
            "el.appendChild(name);" +
            "if (description && description.length > 0) {" +
                "var desc = document.createElement('div');" +
                "desc.textContent = description;" +
                "desc.style.cssText = 'font-size:11px;color:#1F1B16;margin-top:2px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:220px;';" +
                "el.appendChild(desc);" +
            "}" +
            "el.onclick = function(){ onClick(); };" +
            "var marker = new maplibregl.Marker({ element: el, anchor: 'bottom' }).setLngLat([lng, lat]).addTo(map);" +
            "map.__cgMarkers = map.__cgMarkers || [];" +
            "map.__cgMarkers.push(marker);" +
        "})()"
    )
}

private fun jsFitBounds(map: JsAny, west: Double, south: Double, east: Double, north: Double) {
    js("map.fitBounds([[west, south], [east, north]], { padding: 48, animate: true, duration: 300 })")
}

private fun jsResizeMap(map: JsAny) {
    js("map.resize()")
}

private fun jsObserveResize(div: HTMLDivElement, map: JsAny) {
    js("(function(){ map.__cgObs = new ResizeObserver(function(){ map.resize(); }); map.__cgObs.observe(div); })()")
}

private fun jsRemoveMap(map: JsAny) {
    js("map.remove()")
}
