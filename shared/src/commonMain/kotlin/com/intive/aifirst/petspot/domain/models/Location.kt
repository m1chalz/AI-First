package com.intive.aifirst.petspot.domain.models

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a geographic location with search radius and coordinates.
 * Domain model for animal location data.
 *
 * @property city City or area name
 * @property radiusKm Search radius in kilometers
 * @property latitude Latitude coordinate (optional, for map display)
 * @property longitude Longitude coordinate (optional, for map display)
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Location(
    val city: String,
    val radiusKm: Int,
    val latitude: Double? = null,
    val longitude: Double? = null
)

