@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

// Kotlin/Wasm requires every `js("...")` call to be the only expression in its function body
// (no preceding statements, no property accesses inside the JS string). So each public actual
// here forwards to a single-expression `*_impl` helper that takes the inner `JsAny` directly.

internal actual class MapHandle(internal val js: JsAny)

internal actual fun jsMaplibreReady(): Boolean =
    js("(typeof maplibregl !== 'undefined')")

internal actual fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit): Unit =
    js("(el.onload = function() { cb(); })")

internal actual fun jsInjectCriticalCss(): Unit =
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
): JsAny =
    js(
        "(new maplibregl.Map({" +
            "container: containerId," +
            "style: styleUrl," +
            "center: [lng, lat]," +
            "zoom: zoom" +
        "}))"
    )

internal actual fun jsAttachLoadHandler(map: MapHandle, cb: () -> Unit) =
    jsAttachLoadHandlerImpl(map.js, cb)

private fun jsAttachLoadHandlerImpl(m: JsAny, cb: () -> Unit): Unit =
    js("(function(){ if (m.loaded()) { cb(); } else { m.on('load', function(){ cb(); }); } })()")

internal actual fun jsAttachZoomHandler(map: MapHandle, cb: (Double) -> Unit) =
    jsAttachZoomHandlerImpl(map.js, cb)

private fun jsAttachZoomHandlerImpl(m: JsAny, cb: (Double) -> Unit): Unit =
    js("m.on('zoom', function(){ cb(m.getZoom()); })")

internal actual fun jsClearMarkers(map: MapHandle) = jsClearMarkersImpl(map.js)

private fun jsClearMarkersImpl(m: JsAny): Unit =
    js("(function(){ if (m.__cgMarkers) { m.__cgMarkers.forEach(function(x){ x.remove(); }); } m.__cgMarkers = []; })()")

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
): Unit =
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
            "var marker = new maplibregl.Marker({ element: el, anchor: 'bottom' }).setLngLat([lng, lat]).addTo(m);" +
            "m.__cgMarkers = m.__cgMarkers || [];" +
            "m.__cgMarkers.push(marker);" +
        "})()"
    )

internal actual fun jsFitBounds(map: MapHandle, west: Double, south: Double, east: Double, north: Double) =
    jsFitBoundsImpl(map.js, west, south, east, north)

private fun jsFitBoundsImpl(m: JsAny, west: Double, south: Double, east: Double, north: Double): Unit =
    js("m.fitBounds([[west, south], [east, north]], { padding: 48, animate: true, duration: 300 })")

internal actual fun jsResizeMap(map: MapHandle) = jsResizeMapImpl(map.js)

private fun jsResizeMapImpl(m: JsAny): Unit = js("m.resize()")

internal actual fun jsObserveResize(div: HTMLDivElement, map: MapHandle) =
    jsObserveResizeImpl(div, map.js)

private fun jsObserveResizeImpl(div: HTMLDivElement, m: JsAny): Unit =
    js("(function(){ m.__cgObs = new ResizeObserver(function(){ m.resize(); }); m.__cgObs.observe(div); })()")

internal actual fun jsRemoveMap(map: MapHandle) = jsRemoveMapImpl(map.js)

private fun jsRemoveMapImpl(m: JsAny): Unit = js("m.remove()")
