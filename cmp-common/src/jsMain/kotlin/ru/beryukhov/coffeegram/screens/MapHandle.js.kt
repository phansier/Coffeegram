package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

// The JS source strings live as `const val`s in webMain so the wasmJs and js actuals share
// them verbatim. Only the type plumbing (`dynamic` vs `JsAny`) differs between the two.

internal actual class MapHandle(internal val js: dynamic)

internal actual fun jsMaplibreReady(): Boolean = js(JS_MAPLIBRE_READY) as Boolean

internal actual fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit) {
    js(JS_SET_ON_LOAD)
}

internal actual fun jsInjectCriticalCss() {
    js(JS_INJECT_CRITICAL_CSS)
}

internal actual fun jsCreateMap(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): MapHandle = MapHandle(js(JS_CREATE_MAP))

internal actual fun jsAttachLoadHandler(map: MapHandle, cb: () -> Unit) {
    val m = map.js
    js(JS_ATTACH_LOAD_HANDLER)
}

internal actual fun jsAttachZoomHandler(map: MapHandle, cb: (Double) -> Unit) {
    val m = map.js
    js(JS_ATTACH_ZOOM_HANDLER)
}

internal actual fun jsClearMarkers(map: MapHandle) {
    val m = map.js
    js(JS_CLEAR_MARKERS)
}

internal actual fun jsAddMarker(
    map: MapHandle,
    lng: Double,
    lat: Double,
    title: String,
    description: String,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    val m = map.js
    js(JS_ADD_MARKER)
}

internal actual fun jsFitBounds(map: MapHandle, west: Double, south: Double, east: Double, north: Double) {
    val m = map.js
    js(JS_FIT_BOUNDS)
}

internal actual fun jsResizeMap(map: MapHandle) {
    val m = map.js
    js(JS_RESIZE_MAP)
}

internal actual fun jsObserveResize(div: HTMLDivElement, map: MapHandle) {
    val m = map.js
    js(JS_OBSERVE_RESIZE)
}

internal actual fun jsRemoveMap(map: MapHandle) {
    val m = map.js
    js(JS_REMOVE_MAP)
}

internal actual fun jsSetStyle(map: MapHandle, styleUrl: String) {
    val m = map.js
    js(JS_SET_STYLE)
}
