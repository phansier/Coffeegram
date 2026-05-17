@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@file:Suppress("MatchingDeclarationName")

package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

// Kotlin/Wasm requires every `js(...)` call to be the only expression in its function body,
// so each public actual forwards to a single-expression `*_impl` helper that takes the inner
// `JsAny` directly. The JS source strings themselves live as `const val`s in webMain so the
// jsMain actuals share them verbatim.

internal actual class MapHandle(internal val js: JsAny)

internal actual fun jsMaplibreReady(): Boolean = js(JS_MAPLIBRE_READY)

internal actual fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit): Unit = js(JS_SET_ON_LOAD)

internal actual fun jsInjectCriticalCss(): Unit = js(JS_INJECT_CRITICAL_CSS)

internal actual fun jsCreateMap(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): MapHandle = MapHandle(jsCreateMapImpl(containerId, styleUrl, lng, lat, zoom))

private fun jsCreateMapImpl(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): JsAny = js(JS_CREATE_MAP)

internal actual fun jsAttachLoadHandler(map: MapHandle, cb: () -> Unit) =
    jsAttachLoadHandlerImpl(map.js, cb)

private fun jsAttachLoadHandlerImpl(m: JsAny, cb: () -> Unit): Unit = js(JS_ATTACH_LOAD_HANDLER)

internal actual fun jsAttachZoomHandler(map: MapHandle, cb: (Double) -> Unit) =
    jsAttachZoomHandlerImpl(map.js, cb)

private fun jsAttachZoomHandlerImpl(m: JsAny, cb: (Double) -> Unit): Unit = js(JS_ATTACH_ZOOM_HANDLER)

internal actual fun jsClearMarkers(map: MapHandle) = jsClearMarkersImpl(map.js)

private fun jsClearMarkersImpl(m: JsAny): Unit = js(JS_CLEAR_MARKERS)

internal actual fun jsAddMarker(
    map: MapHandle,
    lng: Double,
    lat: Double,
    title: String,
    description: String,
    highlighted: Boolean,
    onClick: () -> Unit,
) = jsAddMarkerImpl(map.js, lng, lat, title, description, highlighted, onClick)

private fun jsAddMarkerImpl(
    m: JsAny,
    lng: Double,
    lat: Double,
    title: String,
    description: String,
    highlighted: Boolean,
    onClick: () -> Unit,
): Unit = js(JS_ADD_MARKER)

internal actual fun jsFitBounds(map: MapHandle, west: Double, south: Double, east: Double, north: Double) =
    jsFitBoundsImpl(map.js, west, south, east, north)

private fun jsFitBoundsImpl(m: JsAny, west: Double, south: Double, east: Double, north: Double): Unit =
    js(JS_FIT_BOUNDS)

internal actual fun jsResizeMap(map: MapHandle) = jsResizeMapImpl(map.js)

private fun jsResizeMapImpl(m: JsAny): Unit = js(JS_RESIZE_MAP)

internal actual fun jsObserveResize(div: HTMLDivElement, map: MapHandle) =
    jsObserveResizeImpl(div, map.js)

private fun jsObserveResizeImpl(div: HTMLDivElement, m: JsAny): Unit = js(JS_OBSERVE_RESIZE)

internal actual fun jsRemoveMap(map: MapHandle) = jsRemoveMapImpl(map.js)

private fun jsRemoveMapImpl(m: JsAny): Unit = js(JS_REMOVE_MAP)

internal actual fun jsSetStyle(map: MapHandle, styleUrl: String) = jsSetStyleImpl(map.js, styleUrl)

private fun jsSetStyleImpl(m: JsAny, styleUrl: String): Unit = js(JS_SET_STYLE)
