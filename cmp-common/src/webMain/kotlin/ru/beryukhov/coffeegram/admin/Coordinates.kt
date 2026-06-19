package ru.beryukhov.coffeegram.admin

import kotlin.math.round

internal fun roundCoordinate(value: Double): Double = round(value * 1e5) / 1e5

internal fun parseCoordinates(input: String): Pair<Double, Double>? {
    val parts = input.split(",")
    if (parts.size != 2) return null
    val latitude = parts[0].trim().toDoubleOrNull() ?: return null
    val longitude = parts[1].trim().toDoubleOrNull() ?: return null
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
    return roundCoordinate(latitude) to roundCoordinate(longitude)
}

internal fun formatCoordinates(latitude: Double, longitude: Double): String =
    "${roundCoordinate(latitude)}, ${roundCoordinate(longitude)}"
