package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

internal actual class MapHandle(internal val js: dynamic)

internal actual fun jsMaplibreReady(): Boolean =
    js("(typeof maplibregl !== 'undefined')") as Boolean

internal actual fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit) {
    js("(el.onload = function() { cb(); })")
}

internal actual fun jsInjectCriticalCss() {
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

internal actual fun jsCreateMap(
    containerId: String,
    styleUrl: String,
    lng: Double,
    lat: Double,
    zoom: Double,
): MapHandle = MapHandle(
    js(
        "(new maplibregl.Map({" +
            "container: containerId," +
            "style: styleUrl," +
            "center: [lng, lat]," +
            "zoom: zoom" +
        "}))"
    )
)

internal actual fun jsAttachLoadHandler(map: MapHandle, cb: () -> Unit) {
    val m = map.js
    js("(function(){ if (m.loaded()) { cb(); } else { m.on('load', function(){ cb(); }); } })()")
}

internal actual fun jsAttachZoomHandler(map: MapHandle, cb: (Double) -> Unit) {
    val m = map.js
    js("m.on('zoom', function(){ cb(m.getZoom()); })")
}

internal actual fun jsClearMarkers(map: MapHandle) {
    val m = map.js
    js("(function(){ if (m.__cgMarkers) { m.__cgMarkers.forEach(function(x){ x.remove(); }); } m.__cgMarkers = []; })()")
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
}

internal actual fun jsFitBounds(map: MapHandle, west: Double, south: Double, east: Double, north: Double) {
    val m = map.js
    js("m.fitBounds([[west, south], [east, north]], { padding: 48, animate: true, duration: 300 })")
}

internal actual fun jsResizeMap(map: MapHandle) {
    val m = map.js
    js("m.resize()")
}

internal actual fun jsObserveResize(div: HTMLDivElement, map: MapHandle) {
    val m = map.js
    js("(function(){ m.__cgObs = new ResizeObserver(function(){ m.resize(); }); m.__cgObs.observe(div); })()")
}

internal actual fun jsRemoveMap(map: MapHandle) {
    val m = map.js
    js("m.remove()")
}
