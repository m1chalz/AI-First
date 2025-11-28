package com.intive.aifirst.petspot.composeapp.domain.models

/**
 * Represents a geographic location with coordinates.
 * Domain model for animal location data.
 *
 * @property latitude Latitude coordinate (optional, may be null if not provided by API)
 * @property longitude Longitude coordinate (optional, may be null if not provided by API)
 */
data class Location(
    val latitude: Double? = null,
    val longitude: Double? = null,
)
