package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Represents a geographic location with search radius and coordinates.
 * Domain model for animal location data.
 *
 * @property city City or area name
 * @property radiusKm Search radius in kilometers
 * @property latitude Latitude coordinate (optional, for map display)
 * @property longitude Longitude coordinate (optional, for map display)
 */
data class Location(
    val city: String,
    val radiusKm: Int,
    // Optional coordinates for map display
    val latitude: Double? = null,
    val longitude: Double? = null,
)
