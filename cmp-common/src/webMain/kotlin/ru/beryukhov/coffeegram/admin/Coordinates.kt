package ru.beryukhov.coffeegram.admin

import kotlin.math.round

internal fun roundCoordinate(value: Double): Double = round(value * 1e5) / 1e5

internal fun parseCoordinates(input: String): Pair<Double, Double>? {
    val parts = input.split(",")
    val latitude = parts.getOrNull(0)?.trim()?.toDoubleOrNull()
    val longitude = parts.getOrNull(1)?.trim()?.toDoubleOrNull()
    return if (
        parts.size == 2 &&
        latitude != null && longitude != null &&
        latitude in -90.0..90.0 && longitude in -180.0..180.0
    ) {
        roundCoordinate(latitude) to roundCoordinate(longitude)
    } else {
        null
    }
}

internal fun formatCoordinates(latitude: Double, longitude: Double): String =
    "${roundCoordinate(latitude)}, ${roundCoordinate(longitude)}"
