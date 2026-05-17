package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

/**
 * Opaque handle to a MapLibre `Map` instance. The implementation differs per target — wasmJs
 * stores a `JsAny`, legacy JS stores a `dynamic` — but the Compose layer treats it as opaque.
 */
internal expect class MapHandle

// JS source snippets shared between the wasmJs and js actuals. Kotlin/Wasm's `js()` accepts a
// `const val` because the compiler resolves the body at compile time; captured variables
// (e.g. `m`, `cb`, `lng`) are matched to whatever names are in scope at the call site.

internal const val JS_MAPLIBRE_READY = "(typeof maplibregl !== 'undefined')"

internal const val JS_SET_ON_LOAD = "(el.onload = function() { cb(); })"

internal const val JS_INJECT_CRITICAL_CSS =
    "(function(){" +
        "var s = document.createElement('style');" +
        "s.textContent = " +
            "'.maplibregl-map{position:relative;overflow:hidden;}' +" +
            "'.maplibregl-canvas-container{position:absolute;left:0;top:0;width:100%;height:100%;}' +" +
            "'.maplibregl-canvas-container canvas{position:absolute;left:0;top:0;}' +" +
            "'.maplibregl-canvas{position:absolute;left:0;top:0;width:100%;height:100%;}';" +
        "document.head.appendChild(s);" +
    "})()"

internal const val JS_CREATE_MAP =
    "(new maplibregl.Map({" +
        "container: containerId," +
        "style: styleUrl," +
        "center: [lng, lat]," +
        "zoom: zoom" +
    "}))"

internal const val JS_ATTACH_LOAD_HANDLER =
    "(function(){ if (m.loaded()) { cb(); } else { m.on('load', function(){ cb(); }); } })()"

internal const val JS_ATTACH_ZOOM_HANDLER =
    "m.on('zoom', function(){ cb(m.getZoom()); })"

internal const val JS_CLEAR_MARKERS =
    "(function(){ if (m.__cgMarkers) { m.__cgMarkers.forEach(function(x){ x.remove(); }); } m.__cgMarkers = []; })()"

internal const val JS_ADD_MARKER =
    "(function(){" +
        "var el = document.createElement('div');" +
        "el.style.cssText = 'display:flex;flex-direction:column;align-items:flex-start;background:' + (highlighted ? " +
        "'#E8E5E3' : '#FFFFFF') + ';border-radius:6px;padding:3px 8px;" +
        "box-shadow:0 2px 3px rgba(0,0,0,0.15),0 6px 9px rgba(0,0,0,0.04);font-family:sans-serif;cursor:pointer;" +
        "max-width:240px;';" +
        "var name = document.createElement('div');" +
        "name.textContent = title;" +
        "name.style.cssText = 'font-size:14px;font-weight:500;color:#1F1B16;white-space:nowrap;overflow:hidden;" +
        "text-overflow:ellipsis;max-width:220px;';" +
        "el.appendChild(name);" +
        "if (description && description.length > 0) {" +
            "var desc = document.createElement('div');" +
            "desc.textContent = description;" +
            "desc.style.cssText = 'font-size:11px;color:#1F1B16;margin-top:2px;white-space:nowrap;overflow:hidden;" +
        "text-overflow:ellipsis;max-width:220px;';" +
            "el.appendChild(desc);" +
        "}" +
        "el.onclick = function(){ onClick(); };" +
        "var marker = new maplibregl.Marker({ element: el, anchor: 'bottom' }).setLngLat([lng, lat]).addTo(m);" +
        "m.__cgMarkers = m.__cgMarkers || [];" +
        "m.__cgMarkers.push(marker);" +
    "})()"

internal const val JS_FIT_BOUNDS =
    "m.fitBounds([[west, south], [east, north]], { padding: 48, animate: true, duration: 300 })"

internal const val JS_RESIZE_MAP = "m.resize()"

internal const val JS_OBSERVE_RESIZE =
    "(function(){ m.__cgObs = new ResizeObserver(function(){ m.resize(); }); m.__cgObs.observe(div); })()"

internal const val JS_REMOVE_MAP = "m.remove()"

internal const val JS_SET_STYLE = "m.setStyle(styleUrl)"

internal expect fun jsMaplibreReady(): Boolean
internal expect fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit)
internal expect fun jsInjectCriticalCss()
internal expect fun jsCreateMap(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): MapHandle
internal expect fun jsAttachLoadHandler(map: MapHandle, cb: () -> Unit)
internal expect fun jsAttachZoomHandler(map: MapHandle, cb: (Double) -> Unit)
internal expect fun jsClearMarkers(map: MapHandle)
internal expect fun jsAddMarker(
    map: MapHandle,
    lng: Double,
    lat: Double,
    title: String,
    description: String,
    highlighted: Boolean,
    onClick: () -> Unit,
)
internal expect fun jsFitBounds(
    map: MapHandle,
    west: Double,
    south: Double,
    east: Double,
    north: Double,
)
internal expect fun jsResizeMap(map: MapHandle)
internal expect fun jsObserveResize(div: HTMLDivElement, map: MapHandle)
internal expect fun jsRemoveMap(map: MapHandle)
internal expect fun jsSetStyle(map: MapHandle, styleUrl: String)
