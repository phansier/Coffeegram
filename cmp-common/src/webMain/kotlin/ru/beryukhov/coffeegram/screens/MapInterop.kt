package ru.beryukhov.coffeegram.screens

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLScriptElement

/**
 * Opaque handle to a MapLibre `Map` instance. The implementation differs per target — wasmJs
 * stores a `JsAny`, legacy JS stores a `dynamic` — but the Compose layer treats it as opaque.
 */
internal expect class MapHandle

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
